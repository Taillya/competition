package com.southwind.vo;

import lombok.Data;

@Data
public class CompetitionVO {
    private Integer id;
    private String title;
    private String type;
    private String icon;
    private String time;
    private Integer participants;
    private String awards;
    private String status;
}
