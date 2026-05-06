package com.southwind.service.impl;

import com.southwind.mapper.RegistrationStatisticsMapper;
import com.southwind.service.RegistrationStatisticsService;
import com.southwind.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RegistrationStatisticsServiceImpl implements RegistrationStatisticsService {

    private static final Map<String, String> DIMENSION_SQL_MAP = new HashMap<>();

    static {
        DIMENSION_SQL_MAP.put("registrationOverview",
                "SELECT COUNT(1) AS total, " +
                        "SUM(CASE WHEN r.status = '通过' THEN 1 ELSE 0 END) AS approvedCount, " +
                        "SUM(CASE WHEN r.status = '待审核' THEN 1 ELSE 0 END) AS pendingCount, " +
                        "SUM(CASE WHEN r.status = '驳回' THEN 1 ELSE 0 END) AS rejectedCount, " +
                        "ROUND(IFNULL(SUM(CASE WHEN r.status = '通过' THEN 1 ELSE 0 END) / NULLIF(COUNT(1), 0), 0) * 100, 2) AS approvalRate " +
                        "FROM registrations r");
        DIMENSION_SQL_MAP.put("registrationByProvince",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS name, COUNT(1) AS value " +
                        "FROM registrations r GROUP BY IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') ORDER BY value DESC");
        DIMENSION_SQL_MAP.put("registrationByProvinceMap",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS name, " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') AS code, COUNT(1) AS value " +
                        "FROM registrations r GROUP BY " +
                        "IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份'), " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') ORDER BY value DESC");
        DIMENSION_SQL_MAP.put("registrationByMonth",
                "SELECT DATE_FORMAT(r.create_time, '%Y-%m') AS name, COUNT(1) AS value " +
                        "FROM registrations r GROUP BY DATE_FORMAT(r.create_time, '%Y-%m') ORDER BY name ASC");
        DIMENSION_SQL_MAP.put("registrationByTrackTopN",
                "SELECT IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS name, COUNT(1) AS value " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id " +
                        "GROUP BY IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') ORDER BY value DESC LIMIT 10");
        DIMENSION_SQL_MAP.put("registrationByTrack",
                "SELECT IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS name, COUNT(1) AS value " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id " +
                        "GROUP BY IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') ORDER BY value DESC");
        DIMENSION_SQL_MAP.put("registrationByStatus",
                "SELECT IFNULL(NULLIF(TRIM(r.status), ''), '待审核') AS name, COUNT(1) AS value " +
                        "FROM registrations r GROUP BY IFNULL(NULLIF(TRIM(r.status), ''), '待审核') ORDER BY value DESC");
        DIMENSION_SQL_MAP.put("registrationDashboardRaw",
                "SELECT IFNULL(NULLIF(TRIM(r.province_name), ''), '未设置省份') AS provinceName, " +
                        "IFNULL(NULLIF(TRIM(r.province_code), ''), '000000') AS provinceCode, " +
                        "IFNULL(NULLIF(TRIM(r.status), ''), '待审核') AS status, " +
                        "IFNULL(NULLIF(TRIM(t.name), ''), '未知赛项') AS trackName, " +
                        "DATE_FORMAT(r.create_time, '%Y-%m') AS month " +
                        "FROM registrations r LEFT JOIN track t ON r.track_id = t.id");
    }

    @Autowired
    private RegistrationStatisticsMapper registrationStatisticsMapper;

    @Override
    public ResultVO queryStatistics(String dimensionCode, Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        String selectSql = DIMENSION_SQL_MAP.get(dimensionCode);
        if (selectSql == null) {
            return new ResultVO(400, "不支持的统计维度: " + dimensionCode + ", requestId=" + requestId, null);
        }
        List<Map<String, Object>> result = this.registrationStatisticsMapper.queryList(selectSql);
        return new ResultVO(200, "查询成功", result);
    }
}
