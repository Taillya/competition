package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Certificate;
import com.southwind.entity.Competition;
import com.southwind.entity.Notice;
import com.southwind.service.CompetitionService;
import com.southwind.util.CommonUtils;
import com.southwind.vo.CertificateVO;
import com.southwind.vo.CompetitionVO;
import com.southwind.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("/list")
    public List<CompetitionVO> list(){
        List<Competition> list = this.competitionService.list();
        List<CompetitionVO> voList = new ArrayList<CompetitionVO>();
        for (Competition competition : list) {
            CompetitionVO vo = new CompetitionVO();
            BeanUtils.copyProperties(competition, vo);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            vo.setTime(simpleDateFormat.format(competition.getTime()));
            voList.add(vo);
        }
        return voList;
    }

    @GetMapping("/load")
    public List<Competition> load(){
        Page<Competition> pageModel = new Page<>(1,3);
        return competitionService.page(pageModel).getRecords();
    }

    @GetMapping("/select")
    public PageVO select(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type){
        Page<Competition> pageModel = new Page<>(page,size);
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        Page<Competition> resultPage = this.competitionService.page(pageModel, queryWrapper);
        List<CompetitionVO> list = new ArrayList<>();
        for (Competition competition : resultPage.getRecords()) {
            CompetitionVO competitionVO = new CompetitionVO();
            BeanUtils.copyProperties(competition, competitionVO);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            competitionVO.setTime(simpleDateFormat.format(competition.getTime()));
            switch (competition.getType()){
                case "tech":
                    competitionVO.setType("科技创新");
                    break;
                case "academic":
                    competitionVO.setType("学术论文");
                    break;
                case "business":
                    competitionVO.setType("创业计划");
                    break;
            }
            switch (competition.getStatus()){
                case "ongoing":
                    competitionVO.setStatus("进行中");
                    break;
                case "upcoming":
                    competitionVO.setStatus("即将开始");
                    break;
                case "ended":
                    competitionVO.setStatus("已结束");
                    break;
                case "awarded":
                    competitionVO.setStatus("已颁奖");
                    break;
            }
            list.add(competitionVO);
        }
        return new PageVO(list,resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody Competition competition){
        competition.setTime(new Date());
        competition.setStatus("upcoming");
        competition.setParticipants(0);
        competition.setIcon(CommonUtils.getIcon(competition.getType()));
        return this.competitionService.save(competition);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Competition competition){
        return this.competitionService.updateById(competition);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return this.competitionService.removeById(id);
    }
}

