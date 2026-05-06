package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.southwind.entity.RegistrationStatsApiConfig;
import com.southwind.mapper.RegistrationStatisticsMapper;
import com.southwind.mapper.RegistrationStatsApiConfigMapper;
import com.southwind.service.RegistrationStatisticsService;
import com.southwind.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RegistrationStatisticsServiceImpl implements RegistrationStatisticsService {

    /** 与 query_sql 中占位一致，服务端替换为 AND YEAR(r.create_time)=… AND competition_id… */
    public static final String STATS_FILTER_MARKER = "/*STATS_FILTER*/";

    /**
     * 未配置库表时的兜底（须含 STATS_FILTER_MARKER，且使用别名 r）。
     */
    private static final Map<String, String> FALLBACK_SQL_MAP = new HashMap<>();

    static {
        FALLBACK_SQL_MAP.put("registrationOverview",
                "SELECT COUNT(1) AS total, " +
                        "SUM(CASE WHEN r.status = '通过' THEN 1 ELSE 0 END) AS approvedCount, " +
                        "SUM(CASE WHEN r.status = '待审核' THEN 1 ELSE 0 END) AS pendingCount, " +
                        "SUM(CASE WHEN r.status = '驳回' THEN 1 ELSE 0 END) AS rejectedCount, " +
                        "ROUND(IFNULL(SUM(CASE WHEN r.status = '通过' THEN 1 ELSE 0 END) / NULLIF(COUNT(1), 0), 0) * 100, 2) AS approvalRate " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER);
        FALLBACK_SQL_MAP.put("registrationByProvince",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS name, COUNT(1) AS value " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') ORDER BY value DESC");
        FALLBACK_SQL_MAP.put("registrationByProvinceMap",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS name, " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') AS code, COUNT(1) AS value " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份'), " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') ORDER BY value DESC");
        FALLBACK_SQL_MAP.put("registrationByMonth",
                "SELECT DATE_FORMAT(r.create_time, '%Y-%m') AS name, COUNT(1) AS value " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY DATE_FORMAT(r.create_time, '%Y-%m') ORDER BY name ASC");
        /* registrationByYear：按自然年汇总，不按页面上选的统计年份过滤，仅可与竞赛筛选组合 */
        FALLBACK_SQL_MAP.put("registrationByYear",
                "SELECT YEAR(r.create_time) AS name, COUNT(1) AS value " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY YEAR(r.create_time) ORDER BY YEAR(r.create_time) ASC");
        FALLBACK_SQL_MAP.put("registrationByTrackTopN",
                "SELECT IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS name, COUNT(1) AS value " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') ORDER BY value DESC LIMIT 10");
        FALLBACK_SQL_MAP.put("registrationByTrack",
                "SELECT IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS name, COUNT(1) AS value " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') ORDER BY value DESC");
        FALLBACK_SQL_MAP.put("registrationByStatus",
                "SELECT IFNULL(NULLIF(TRIM(r.status), ''), '待审核') AS name, COUNT(1) AS value " +
                        "FROM registrations r WHERE 1=1 " + STATS_FILTER_MARKER +
                        " GROUP BY IFNULL(NULLIF(TRIM(r.status), ''), '待审核') ORDER BY value DESC");
        FALLBACK_SQL_MAP.put("registrationDashboardRaw",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS provinceName, " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') AS provinceCode, " +
                        "IFNULL(NULLIF(TRIM(r.status), ''), '待审核') AS status, " +
                        "IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS trackName, " +
                        "DATE_FORMAT(r.create_time, '%Y-%m') AS month " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id WHERE 1=1 " + STATS_FILTER_MARKER);
    }

    @Autowired
    private RegistrationStatisticsMapper registrationStatisticsMapper;

    @Autowired
    private RegistrationStatsApiConfigMapper registrationStatsApiConfigMapper;

    @Override
    public ResultVO queryStatistics(String apiCode, Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        int year = resolveYear(params);
        Integer competitionId = resolveCompetitionId(params);

        RegistrationStatsApiConfig cfg = registrationStatsApiConfigMapper.selectOne(
                new LambdaQueryWrapper<RegistrationStatsApiConfig>()
                        .eq(RegistrationStatsApiConfig::getApiCode, apiCode)
                        .last("LIMIT 1"));
        String selectSql = cfg != null && cfg.getQuerySql() != null && !cfg.getQuerySql().isEmpty()
                ? cfg.getQuerySql().trim()
                : FALLBACK_SQL_MAP.get(apiCode);
        if (selectSql == null) {
            return new ResultVO(400, "不支持的统计维度: " + apiCode + ", requestId=" + requestId, null);
        }
        boolean applyYearFilter = !"registrationByYear".equals(apiCode);
        selectSql = applyStatsFilter(selectSql, year, competitionId, applyYearFilter);
        try {
            List<Map<String, Object>> result = this.registrationStatisticsMapper.queryList(selectSql);
            return new ResultVO(200, "查询成功", result);
        } catch (Exception e) {
            return new ResultVO(500, "统计查询失败: " + e.getMessage(), null);
        }
    }

    private int resolveYear(Map<String, Object> params) {
        if (params == null) {
            return Year.now().getValue();
        }
        Object y = params.get("year");
        if (y == null) {
            return Year.now().getValue();
        }
        if (y instanceof Number) {
            return ((Number) y).intValue();
        }
        try {
            return Integer.parseInt(y.toString().trim());
        } catch (NumberFormatException e) {
            return Year.now().getValue();
        }
    }

    private Integer resolveCompetitionId(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        Object c = params.get("competitionId");
        if (c == null || "".equals(c)) {
            return null;
        }
        if (c instanceof Number) {
            int v = ((Number) c).intValue();
            return v <= 0 ? null : v;
        }
        try {
            int v = Integer.parseInt(c.toString().trim());
            return v <= 0 ? null : v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String applyStatsFilter(String sql, int year, Integer competitionId, boolean applyYearFilter) {
        StringBuilder cond = new StringBuilder();
        if (applyYearFilter) {
            cond.append(" AND YEAR(r.create_time) = ").append(year);
        }
        if (competitionId != null) {
            cond.append(" AND r.competition_id = ").append(competitionId);
        }
        if (sql.contains(STATS_FILTER_MARKER)) {
            return sql.replace(STATS_FILTER_MARKER, cond.toString());
        }
        return sql;
    }
}
