package com.southwind.vo;

import lombok.Data;

@Data
public class CompetitionVO {
    private Integer id;
    private String title;
    private String type;
    private String icon;
    private String time;
    /** 口径 A：本平台该竞赛「已通过」报名数 */
    private Integer participants;
    private String awards;
    private String status;
    private Integer registrationEnabled;
    private String registrationEntryMode;
    private String registrationStart;
    private String registrationEnd;
    /** DISABLED | NOT_STARTED | ENDED | OPEN */
    private String registrationPhase;
}
