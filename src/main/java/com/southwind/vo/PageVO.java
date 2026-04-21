package com.southwind.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageVO {
    private List data;
    private Long size;
    private Long total;
}
