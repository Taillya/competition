package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.entity.Tag;
import com.southwind.entity.Track;
import com.southwind.entity.TrackTag;
import com.southwind.form.TrackForm;
import com.southwind.mapper.TagMapper;
import com.southwind.mapper.TrackMapper;
import com.southwind.service.TrackService;
import com.southwind.service.TrackTagService;
import com.southwind.vo.PageVO;
import com.southwind.vo.TrackVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/track")
public class TrackController {

    @Autowired
    private TrackService trackService;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TrackTagService trackTagService;

    @GetMapping("/list")
    public List<TrackVO> list(){
        return this.trackService.trackVOList();
    }

    /**
     * 学生端：某竞赛下的赛道列表（用于报名链路）。
     */
    @GetMapping("/byCompetition")
    public List<TrackVO> byCompetition(@RequestParam("competitionId") Integer competitionId) {
        return this.trackService.listVoByCompetitionId(competitionId);
    }

    @GetMapping("/load")
    public PageVO load(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type,
            @RequestParam(value = "competitionId", required = false) Integer competitionId
    ){
        Page<Track> pageModel = new Page<>(page,size);
        QueryWrapper<Track> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        queryWrapper.eq(competitionId != null, "competition_id", competitionId);
        Page<Track> resultPage = this.trackService.page(pageModel, queryWrapper);
        List<TrackVO> trackVOList = new ArrayList<>();
        for (Track track : resultPage.getRecords()) {
            TrackVO trackVO = new TrackVO();
            BeanUtils.copyProperties(track, trackVO);
            List<Tag> tagList = this.tagMapper.getTagsForManage(track.getId());
            trackVO.setTagsForManage(tagList);
            trackVOList.add(trackVO);
        }
        return new PageVO(trackVOList,resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody TrackForm trackForm){
        if (trackForm.getCompetitionId() == null) {
            return false;
        }
        Track track = new Track();
        BeanUtils.copyProperties(trackForm,track);
        Boolean trackAdd = this.trackService.save(track);
        Integer[] tag = trackForm.getTag();
        List<TrackTag> list = new ArrayList<>();
        for (Integer id : tag) {
            TrackTag trackTag = new TrackTag();
            trackTag.setTrackId(track.getId());
            trackTag.setTagId(id);
            list.add(trackTag);
        }
        boolean saveBatch = this.trackTagService.saveBatch(list);
        return trackAdd && saveBatch;
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody TrackForm trackForm){
        if (trackForm.getCompetitionId() == null) {
            return false;
        }
        Track track = new Track();
        BeanUtils.copyProperties(trackForm,track);
        Boolean trackUpdate = this.trackService.updateById(track);
        Integer[] tag = trackForm.getTag();
        List<TrackTag> list = new ArrayList<>();
        for (Integer id : tag) {
            TrackTag trackTag = new TrackTag();
            trackTag.setTrackId(track.getId());
            trackTag.setTagId(id);
            list.add(trackTag);
        }
        this.tagMapper.clearTagsByTrackId(track.getId());
        boolean saveBatch = this.trackTagService.saveBatch(list);
        return trackUpdate && saveBatch;
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        this.tagMapper.clearTagsByTrackId(id);
        return this.trackService.removeById(id);
    }
}

