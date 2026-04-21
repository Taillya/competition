package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.entity.Registrations;
import com.southwind.entity.Track;
import com.southwind.service.RegistrationsService;
import com.southwind.service.TrackService;
import com.southwind.vo.PageVO;
import com.southwind.vo.RegistrationsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
@RequestMapping("/registrations")
public class RegistrationsController {

    @Autowired
    private RegistrationsService registrationsService;
    @Autowired
    private TrackService trackService;

    @PostMapping("/add")
    public Boolean add(@RequestBody Registrations registrations) {
        registrations.setCreateTime(new Date());
        return this.registrationsService.save(registrations);
    }

    @GetMapping("/list")
    public PageVO list(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type
    ){
        Page<Registrations> pageModel = new Page<>(page,size);
        QueryWrapper<Registrations> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        Page<Registrations> resultPage = this.registrationsService.page(pageModel, queryWrapper);
        List<RegistrationsVO> list = new ArrayList<>();
        for (Registrations record : resultPage.getRecords()) {
            RegistrationsVO vo = new RegistrationsVO();
            BeanUtils.copyProperties(record,vo);
            Track track = this.trackService.getById(record.getTrackId());
            vo.setTrackName(track.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            vo.setDate(sdf.format(record.getCreateTime()));
            list.add(vo);
        }
        return new PageVO(list,resultPage.getSize(),resultPage.getTotal());
    }
}

