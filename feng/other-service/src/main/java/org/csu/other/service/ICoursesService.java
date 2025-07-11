package org.csu.other.service;

import org.csu.other.entity.po.Courses;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-07-05
 */
public interface ICoursesService extends IService<Courses> {

    String getCourseName(Long courseId);

}
