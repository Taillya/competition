package com.southwind.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateVO {
    private Integer id;
    private String title;
    private String competition;
    private String level;
    private String published;
    private Boolean publish;
    private String organization;
    private String date;
    private String color;
}
