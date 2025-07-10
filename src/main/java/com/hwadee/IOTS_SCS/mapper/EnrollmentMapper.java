package com.hwadee.IOTS_SCS.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnrollmentMapper {
    List<Long> getStudentsIdsByCourseId(Long courseId);

    List<Long> getCourseIdsByUid(Long uid);
}
