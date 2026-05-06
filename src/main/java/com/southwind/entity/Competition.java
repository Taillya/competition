package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    public class Competition implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private String title;

    private String type;

    private String icon;

    private Date time;

    private Integer participants;

    private String awards;

    private String status;

    /** 是否在本平台开放网上报名：0 否 1 是 */
    private Integer registrationEnabled;

    /**
     * DIRECT：直达报名表（该竞赛下须仅一条赛道）；SELECT_TRACK：先选择赛道。
     */
    private String registrationEntryMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationEnd;


}
