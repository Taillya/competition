package com.southwind.vo;

import com.southwind.entity.Registrations;
import lombok.Data;

@Data
public class RegistrationsVO extends Registrations {
    private String trackName;
    private String competitionName;
    private String date;
}
