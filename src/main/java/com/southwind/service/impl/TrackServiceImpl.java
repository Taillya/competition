package com.southwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.Tag;
import com.southwind.entity.Track;
import com.southwind.mapper.TagMapper;
import com.southwind.mapper.TrackMapper;
import com.southwind.service.TrackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.vo.TrackVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2025-03-26
 */
@Service
public class TrackServiceImpl extends ServiceImpl<TrackMapper, Track> implements TrackService {

    @Autowired
    private TrackMapper trackMapper;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TrackVO> trackVOList() {
        List<Track> trackList = this.trackMapper.selectList(null);
        List<TrackVO> trackVOList = new ArrayList<>();
        for (Track track : trackList) {
            TrackVO trackVO = new TrackVO();
            BeanUtils.copyProperties(track, trackVO);
            List<String> tagList = this.tagMapper.getTagsByTrackId(track.getId());
            String[] tags = tagList.toArray(new String[tagList.size()]);
            trackVO.setTags(tags);
            trackVOList.add(trackVO);
        }
        return trackVOList;
    }

    @Override
    public List<TrackVO> listVoByCompetitionId(Integer competitionId) {
        QueryWrapper<Track> qw = new QueryWrapper<>();
        qw.eq("competition_id", competitionId).orderByAsc("id");
        List<Track> trackList = this.trackMapper.selectList(qw);
        List<TrackVO> trackVOList = new ArrayList<>();
        for (Track track : trackList) {
            TrackVO trackVO = new TrackVO();
            BeanUtils.copyProperties(track, trackVO);
            List<String> tagList = this.tagMapper.getTagsByTrackId(track.getId());
            String[] tags = tagList.toArray(new String[tagList.size()]);
            trackVO.setTags(tags);
            trackVOList.add(trackVO);
        }
        return trackVOList;
    }
}
