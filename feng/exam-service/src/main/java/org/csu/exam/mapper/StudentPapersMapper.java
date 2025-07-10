package org.csu.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.exam.entity.po.StudentPapers;

/**
 * <p>
 * 学生与试卷的关联表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-06-27
 */
@Mapper
public interface StudentPapersMapper extends BaseMapper<StudentPapers> {

}
