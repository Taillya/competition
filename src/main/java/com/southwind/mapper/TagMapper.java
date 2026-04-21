package com.southwind.mapper;

import com.southwind.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2025-03-26
 */
public interface TagMapper extends BaseMapper<Tag> {

    @Select({"select tag.text from track,tag,track_tag where track.id = track_tag.track_id and tag.id = track_tag.tag_id and track.id = #{trackId}"})
    public List<String> getTagsByTrackId(Integer trackId);

    @Select({"select tag.id,tag.text from track,tag,track_tag where track.id = track_tag.track_id and tag.id = track_tag.tag_id and track.id = #{trackId}"})
    public List<Tag> getTagsForManage(Integer trackId);

    @Delete({"delete from track_tag where track_id = #{trackId}"})
    public int clearTagsByTrackId(Integer trackId);

    @Delete({"delete from track_tag where tag_id = #{tagId}"})
    public int clearTagsByTagId(Integer tagId);
}
