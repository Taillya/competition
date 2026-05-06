package com.southwind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

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
    public class Registrations implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private Integer trackId;

    private String teamName;

    private String name;

    private String idCard;

    private String phone;

    private String email;

    private String submitterUserId;

    private String provinceCode;

    private String provinceName;

    private String status;

    private Date auditTime;

    private Date createTime;


}
