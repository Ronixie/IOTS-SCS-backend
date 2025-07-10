package org.csu.other.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.csu.other.entity.po.StudentCourse;
import org.csu.other.mapper.StudentCourseMapper;
import org.csu.other.service.IStudentCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-07-05
 */
@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements IStudentCourseService {

    @Override
    public List<Long> getStudentIdsByCourseId(Long courseId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getCourseId, courseId);
        List<StudentCourse> studentCourses = list(wrapper);
        return studentCourses.stream().map(StudentCourse::getStudentId).toList();
    }

    @Override
    public List<Long> getCourseIdsByUid(Long uid) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getStudentId, uid);
        List<StudentCourse> studentCourses = list(wrapper);
        return studentCourses.stream().map(StudentCourse::getCourseId).toList();
    }
}
