package com.southwind.mapper;

import com.southwind.entity.Registrations;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
public interface RegistrationsMapper extends BaseMapper<Registrations> {

    /**
     * 口径 A：已通过审核的报名条数（按竞赛聚合）。
     */
    @Select("SELECT COUNT(1) FROM registrations r INNER JOIN track t ON r.track_id = t.id "
            + "WHERE t.competition_id = #{competitionId} AND r.status = '通过'")
    Long countApprovedByCompetitionId(@Param("competitionId") Integer competitionId);
}
