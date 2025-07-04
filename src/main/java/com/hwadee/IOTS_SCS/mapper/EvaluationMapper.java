package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hwadee.IOTS_SCS.entity.POJO.Enrollment;
import com.hwadee.IOTS_SCS.entity.POJO.Progress;

import java.time.LocalDateTime;
import java.util.Date;

public interface EvaluationMapper extends BaseMapper<Object> {
    IPage<Enrollment> selectEnrollment(IPage<Enrollment> page, String uid, LocalDateTime fromTime);
    IPage<Progress> selectProgress(IPage<Progress> page, String uid, LocalDateTime fromTime);
}
