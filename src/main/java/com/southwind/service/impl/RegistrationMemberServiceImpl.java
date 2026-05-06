package com.southwind.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.entity.RegistrationMember;
import com.southwind.mapper.RegistrationMemberMapper;
import com.southwind.service.RegistrationMemberService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationMemberServiceImpl extends ServiceImpl<RegistrationMemberMapper, RegistrationMember> implements RegistrationMemberService {
}
