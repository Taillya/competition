package com.southwind.service.impl;

import com.southwind.entity.Competition;
import com.southwind.mapper.CompetitionMapper;
import com.southwind.mapper.RegistrationsMapper;
import com.southwind.service.CompetitionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    @Autowired
    private RegistrationsMapper registrationsMapper;

    @Override
    public String resolveRegistrationPhase(Competition c) {
        if (c == null || c.getRegistrationEnabled() == null || c.getRegistrationEnabled() == 0) {
            return "DISABLED";
        }
        Date now = new Date();
        if (c.getRegistrationStart() != null && now.before(c.getRegistrationStart())) {
            return "NOT_STARTED";
        }
        if (c.getRegistrationEnd() != null && now.after(c.getRegistrationEnd())) {
            return "ENDED";
        }
        return "OPEN";
    }

    @Override
    public boolean isRegistrationOpen(Competition c) {
        return "OPEN".equals(resolveRegistrationPhase(c));
    }

    @Override
    public int countApprovedByCompetition(Integer competitionId) {
        Long n = this.registrationsMapper.countApprovedByCompetitionId(competitionId);
        return n == null ? 0 : n.intValue();
    }
}
