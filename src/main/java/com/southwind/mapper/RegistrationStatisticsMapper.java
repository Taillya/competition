package com.southwind.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 报名业务多维统计（动态 SQL 由服务层拼装，仅执行查询）。
 * 使用字面替换而非 {@code <script>}，避免部分 SQL 被当作 XML 解析导致执行异常。
 */
public interface RegistrationStatisticsMapper {

    @Select("${selectSql}")
    List<Map<String, Object>> queryList(@Param("selectSql") String selectSql);
}
