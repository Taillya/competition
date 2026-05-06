package com.southwind.service;

import com.southwind.vo.ResultVO;

import java.util.Map;

/**
 * 竞赛平台 — 报名数据统计（管理端看板等）。
 */
public interface RegistrationStatisticsService {

    /**
     * 按预置维度代码查询统计结果（省份分布、概览、赛道等）。
     *
     * @param dimensionCode 维度代码，与 SQL 模板映射键一致
     * @param params        预留扩展参数（当前未使用）
     */
    ResultVO queryStatistics(String dimensionCode, Map<String, Object> params);
}
