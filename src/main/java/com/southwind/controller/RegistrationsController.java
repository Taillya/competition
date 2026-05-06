package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Competition;
import com.southwind.entity.RegistrationMember;
import com.southwind.entity.Registrations;
import com.southwind.entity.Track;
import com.southwind.form.RegistrationForm;
import com.southwind.form.RegistrationMemberForm;
import com.southwind.service.CompetitionService;
import com.southwind.service.RegistrationMemberService;
import com.southwind.service.RegistrationsService;
import com.southwind.service.TrackService;
import com.southwind.vo.PageVO;
import com.southwind.vo.RegistrationsVO;
import com.southwind.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationsController {

    @Autowired
    private RegistrationsService registrationsService;
    @Autowired
    private TrackService trackService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private RegistrationMemberService registrationMemberService;
    @Value("${platform.registration.auto-approve:false}")
    private Boolean defaultAutoApprove;
    private Boolean autoApproveOverride = null;

    private Boolean isAutoApproveEnabled() {
        return this.autoApproveOverride != null ? this.autoApproveOverride : this.defaultAutoApprove;
    }

    private void fillCompetitionName(RegistrationsVO vo, Registrations record) {
        Integer cid = record.getCompetitionId();
        if (cid == null && record.getTrackId() != null) {
            Track t = this.trackService.getById(record.getTrackId());
            if (t != null) {
                cid = t.getCompetitionId();
            }
        }
        if (cid != null) {
            Competition c = this.competitionService.getById(cid);
            vo.setCompetitionName(c != null ? c.getTitle() : "未知竞赛");
        } else {
            vo.setCompetitionName("-");
        }
    }

    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(@RequestBody RegistrationForm form) {
        Track track = this.trackService.getById(form.getTrackId());
        if (track == null) {
            return false;
        }
        Competition competition = this.competitionService.getById(track.getCompetitionId());
        if (competition == null) {
            return false;
        }
        if (form.getCompetitionId() != null && !form.getCompetitionId().equals(track.getCompetitionId())) {
            return false;
        }
        if (!this.competitionService.isRegistrationOpen(competition)) {
            return false;
        }
        if ("DIRECT".equalsIgnoreCase(competition.getRegistrationEntryMode())) {
            long trackCount = this.trackService.count(new QueryWrapper<Track>().eq("competition_id", competition.getId()));
            if (trackCount != 1) {
                return false;
            }
        }
        form.setCreateTime(new Date());
        if (StringUtils.isBlank(form.getStatus())) {
            if (Boolean.TRUE.equals(isAutoApproveEnabled())) {
                form.setStatus("通过");
                form.setAuditTime(new Date());
            } else {
                form.setStatus("待审核");
                form.setAuditTime(null);
            }
        } else if (("通过".equals(form.getStatus()) || "驳回".equals(form.getStatus()))
                && form.getAuditTime() == null) {
            form.setAuditTime(new Date());
        }
        if (StringUtils.isBlank(form.getSubmitterUserId()) || StringUtils.isBlank(form.getIdCard())) {
            return false;
        }
        List<RegistrationMemberForm> members = form.getMembers();
        if (members == null || members.isEmpty()) {
            return false;
        }
        form.setCompetitionId(track.getCompetitionId());
        Registrations registrations = new Registrations();
        BeanUtils.copyProperties(form, registrations);
        boolean saved = this.registrationsService.save(registrations);
        if (!saved || registrations.getId() == null) {
            return false;
        }
        List<RegistrationMember> memberEntities = new ArrayList<>();
        for (RegistrationMemberForm member : members) {
            if (StringUtils.isBlank(member.getMemberName()) || StringUtils.isBlank(member.getMemberIdCard())) {
                continue;
            }
            RegistrationMember entity = new RegistrationMember();
            entity.setRegistrationId(registrations.getId());
            entity.setMemberName(member.getMemberName());
            entity.setMemberIdCard(member.getMemberIdCard());
            memberEntities.add(entity);
        }
        if (memberEntities.isEmpty()) {
            return false;
        }
        return this.registrationMemberService.saveBatch(memberEntities);
    }

    @GetMapping("/audit-strategy")
    public ResultVO auditStrategy() {
        Map<String, Object> data = new HashMap<>();
        data.put("autoApprove", isAutoApproveEnabled());
        data.put("defaultAutoApprove", this.defaultAutoApprove);
        return new ResultVO(200, "查询成功", data);
    }

    @PutMapping("/audit-strategy")
    public ResultVO updateAuditStrategy(@RequestParam("autoApprove") Boolean autoApprove) {
        this.autoApproveOverride = autoApprove;
        Map<String, Object> data = new HashMap<>();
        data.put("autoApprove", isAutoApproveEnabled());
        return new ResultVO(200, "更新成功", data);
    }

    @GetMapping("/list")
    public PageVO list(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type,
            @RequestParam(value = "competitionId", required = false) Integer competitionId
    ){
        Page<Registrations> pageModel = new Page<>(page,size);
        QueryWrapper<Registrations> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        queryWrapper.eq(competitionId != null, "competition_id", competitionId);
        queryWrapper.orderByDesc("create_time");
        Page<Registrations> resultPage = this.registrationsService.page(pageModel, queryWrapper);
        List<RegistrationsVO> list = new ArrayList<>();
        for (Registrations record : resultPage.getRecords()) {
            RegistrationsVO vo = new RegistrationsVO();
            BeanUtils.copyProperties(record,vo);
            Track track = this.trackService.getById(record.getTrackId());
            vo.setTrackName(track != null ? track.getName() : "未知赛道");
            fillCompetitionName(vo, record);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            vo.setDate(sdf.format(record.getCreateTime()));
            list.add(vo);
        }
        return new PageVO(list,resultPage.getSize(),resultPage.getTotal());
    }

    @GetMapping("/my")
    public ResultVO myRegistrations(@RequestParam("submitterUserId") String submitterUserId){
        QueryWrapper<Registrations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("submitter_user_id", submitterUserId);
        queryWrapper.orderByDesc("create_time");
        List<Registrations> records = this.registrationsService.list(queryWrapper);
        List<RegistrationsVO> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Registrations record : records) {
            RegistrationsVO vo = new RegistrationsVO();
            BeanUtils.copyProperties(record, vo);
            Track track = this.trackService.getById(record.getTrackId());
            vo.setTrackName(track != null ? track.getName() : "未知赛道");
            fillCompetitionName(vo, record);
            vo.setDate(record.getCreateTime() != null ? sdf.format(record.getCreateTime()) : "");
            list.add(vo);
        }
        return new ResultVO(200, "查询成功", list);
    }

    @GetMapping("/members/{registrationId}")
    public ResultVO members(@PathVariable("registrationId") Integer registrationId) {
        QueryWrapper<RegistrationMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("registration_id", registrationId).orderByAsc("id");
        List<RegistrationMember> members = this.registrationMemberService.list(queryWrapper);
        return new ResultVO(200, "查询成功", members);
    }

    @PutMapping("/audit/{id}")
    public ResultVO audit(@PathVariable("id") Integer id, @RequestParam("status") String status) {
        if (!"通过".equals(status) && !"驳回".equals(status)) {
            return new ResultVO(400, "状态非法，仅支持 通过/驳回", null);
        }
        Registrations registrations = this.registrationsService.getById(id);
        if (registrations == null) {
            return new ResultVO(404, "报名记录不存在", null);
        }
        registrations.setStatus(status);
        registrations.setAuditTime(new Date());
        boolean updated = this.registrationsService.updateById(registrations);
        return updated ? new ResultVO(200, "审核完成", null) : new ResultVO(500, "审核失败", null);
    }

}

