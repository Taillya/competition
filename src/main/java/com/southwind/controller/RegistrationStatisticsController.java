package com.southwind.controller;

import com.southwind.service.RegistrationStatisticsService;
import com.southwind.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 竞赛平台 — 管理员报名数据可视化 / 看板统计接口。
 */
@RestController
@RequestMapping("/registrations/statistics")
public class RegistrationStatisticsController {

    @Autowired
    private RegistrationStatisticsService registrationStatisticsService;

    /**
     * 按维度查询聚合数据（registrationOverview、registrationByProvince 等）。
     */
    @PostMapping("/{dimension}")
    public ResultVO query(@PathVariable("dimension") String dimension,
                          @RequestBody Map<String, Object> params) {
        return this.registrationStatisticsService.queryStatistics(dimension, params);
    }
}
