package com.southwind.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 报名业务多维统计（动态 SQL 片段由服务层注册，仅执行查询）。
 */
public interface RegistrationStatisticsMapper {

    @Select({"<script>", "${selectSql}", "</script>"})
    List<Map<String, Object>> queryList(@Param("selectSql") String selectSql);
}
