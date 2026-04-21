package com.southwind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultVO {
    private Integer code;
    private String msg;
    private Object data;
}
