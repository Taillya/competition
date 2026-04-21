package com.southwind.vo;

import com.southwind.entity.Tag;
import com.southwind.entity.Track;
import lombok.Data;

import java.util.List;

@Data
public class TrackVO extends Track {
    private String[] tags;
    private List<Tag> tagsForManage;
}
