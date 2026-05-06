package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Competition;
import com.southwind.service.CompetitionService;
import com.southwind.util.CommonUtils;
import com.southwind.vo.CompetitionVO;
import com.southwind.vo.PageVO;
import com.southwind.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private void fillRegistrationExtra(Competition competition, CompetitionVO vo) {
        vo.setParticipants(this.competitionService.countApprovedByCompetition(competition.getId()));
        vo.setRegistrationPhase(this.competitionService.resolveRegistrationPhase(competition));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (competition.getRegistrationStart() != null) {
            vo.setRegistrationStart(sdf.format(competition.getRegistrationStart()));
        }
        if (competition.getRegistrationEnd() != null) {
            vo.setRegistrationEnd(sdf.format(competition.getRegistrationEnd()));
        }
    }

    @GetMapping("/list")
    public List<CompetitionVO> list(){
        List<Competition> list = this.competitionService.list();
        List<CompetitionVO> voList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Competition competition : list) {
            CompetitionVO vo = new CompetitionVO();
            BeanUtils.copyProperties(competition, vo);
            if (competition.getTime() != null) {
                vo.setTime(simpleDateFormat.format(competition.getTime()));
            }
            fillRegistrationExtra(competition, vo);
            voList.add(vo);
        }
        return voList;
    }

    @GetMapping("/detail/{id}")
    public ResultVO detail(@PathVariable("id") Integer id) {
        Competition competition = this.competitionService.getById(id);
        if (competition == null) {
            return new ResultVO(404, "竞赛不存在", null);
        }
        CompetitionVO vo = new CompetitionVO();
        BeanUtils.copyProperties(competition, vo);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (competition.getTime() != null) {
            vo.setTime(simpleDateFormat.format(competition.getTime()));
        }
        fillRegistrationExtra(competition, vo);
        return new ResultVO(200, "查询成功", vo);
    }

    /**
     * 未带 competitionId 访问报名页时的兜底：优先返回首个开启报名的竞赛。
     */
    @GetMapping("/defaultForRegistration")
    public ResultVO defaultForRegistration() {
        QueryWrapper<Competition> qw = new QueryWrapper<>();
        qw.eq("registration_enabled", 1).orderByAsc("id").last("limit 1");
        Competition competition = this.competitionService.getOne(qw);
        if (competition == null) {
            competition = this.competitionService.getById(1);
        }
        if (competition == null) {
            return new ResultVO(404, "暂无竞赛数据", null);
        }
        CompetitionVO vo = new CompetitionVO();
        BeanUtils.copyProperties(competition, vo);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (competition.getTime() != null) {
            vo.setTime(simpleDateFormat.format(competition.getTime()));
        }
        fillRegistrationExtra(competition, vo);
        return new ResultVO(200, "查询成功", vo);
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
            if (competition.getTime() != null) {
                competitionVO.setTime(simpleDateFormat.format(competition.getTime()));
            }
            fillRegistrationExtra(competition, competitionVO);
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
        if (competition.getTime() == null) {
            competition.setTime(new Date());
        }
        if (competition.getStatus() == null) {
            competition.setStatus("upcoming");
        }
        if (competition.getParticipants() == null) {
            competition.setParticipants(0);
        }
        if (competition.getRegistrationEntryMode() == null || competition.getRegistrationEntryMode().isEmpty()) {
            competition.setRegistrationEntryMode("SELECT_TRACK");
        }
        if (competition.getRegistrationEnabled() == null) {
            competition.setRegistrationEnabled(0);
        }
        if (competition.getIcon() == null || competition.getIcon().isEmpty()) {
            competition.setIcon(CommonUtils.getIcon(competition.getType()));
        }
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

