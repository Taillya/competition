package com.southwind.service;

import com.southwind.entity.Track;
import com.baomidou.mybatisplus.extension.service.IService;
import com.southwind.vo.TrackVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2025-03-26
 */
public interface TrackService extends IService<Track> {
    public List<TrackVO> trackVOList();
}
