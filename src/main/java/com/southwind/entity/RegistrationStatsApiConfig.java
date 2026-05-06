package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 报名统计接口 SQL 配置（api_code 与前端路径维度一致）。
 * query_sql 中须包含占位符 STATS_FILTER（见 RegistrationStatisticsServiceImpl 常量），通常写在 WHERE 1=1 之后。
 */
@Data
@TableName("registration_stats_api_config")
public class RegistrationStatsApiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 与 POST /registrations/statistics/{api_code} 路径一致 */
    private String apiCode;

    /** 完整 SELECT，须含 WHERE 1=1 及 STATS_FILTER 占位符（写法见 RegistrationStatisticsServiceImpl，勿在注释里写 slash-star） */
    private String querySql;
}
