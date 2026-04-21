package com.southwind.form;

import com.southwind.entity.Track;
import lombok.Data;

@Data
public class TrackForm extends Track {
    private Integer[] tag;
}
