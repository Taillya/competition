package com.southwind.form;

import com.southwind.entity.Registrations;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationForm extends Registrations {
    private List<RegistrationMemberForm> members;
}
