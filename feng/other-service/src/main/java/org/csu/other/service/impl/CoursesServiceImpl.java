package org.csu.other.service.impl;

import org.csu.other.entity.po.Courses;
import org.csu.other.mapper.CoursesMapper;
import org.csu.other.service.ICoursesService;
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
public class CoursesServiceImpl extends ServiceImpl<CoursesMapper, Courses> implements ICoursesService {

    @Override
    public String getCourseName(Long courseId) {
        Courses course = getById(courseId);
        if(course!=null){
            return course.getCourseName();
        }else{
            return null;
        }
    }
}
