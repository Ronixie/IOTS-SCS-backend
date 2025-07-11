package org.csu.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.exam.entity.po.StudentPapers;
import org.csu.exam.mapper.StudentPapersMapper;
import org.csu.exam.service.IStudentPapersService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学生与试卷的关联表 服务实现类
 * </p>
 *
 * @author
 * @since 2025-06-27
 */
@Service
public class StudentPapersServiceImpl extends ServiceImpl<StudentPapersMapper, StudentPapers> implements IStudentPapersService {

    /**
     * 判断学生与试卷是否相关
     *
     * @param userId  学生id
     * @param paperId 试卷id
     * @return true or false
     */
    @Override
    public boolean exists(long userId, String paperId) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getPaperId, paperId);
        StudentPapers studentPapers = getOne(wrapper);
        return studentPapers != null;
    }

    /**
     * 更新试卷状态
     *
     * @param userId  学生id
     * @param paperId 试卷id
     * @param status  状态
     * @return true or false
     */
    @Override
    public boolean updateStatus(long userId, String paperId, int status) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getPaperId, paperId);
        StudentPapers sp = getOne(wrapper);
        sp.setStatus(status);
        sp.setSubmitTime(LocalDateTime.now());
        return update(sp, wrapper);
    }

    /**
     * 获取提交时间
     *
     * @param userId 学生id
     * @param paperId     试卷id
     * @return 提交时间|null
     */
    @Override
    public LocalDateTime getSubmitTime(long userId, String paperId) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getPaperId, paperId)
                .eq(StudentPapers::getStatus, 1);
        StudentPapers sp = getOne(wrapper);
        if (sp != null) {
            return sp.getSubmitTime();
        } else {
            return null;
        }
    }

    /**
     * 更新学生试卷分数
     * @param userId
     * @param paperId
     * @param totalScore
     * @return
     */
    @Override
    public boolean updateScore(long userId, String paperId, double totalScore) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getPaperId, paperId);
        StudentPapers sp = getOne(wrapper);
        sp.setTotalScore(totalScore);
        return update(sp, wrapper);
    }

    /**
     *
     * @param userId
     * @param paperId
     * @return
     */
    @Override
    public StudentPapers getByUserIdAndPaperId(long userId, String paperId) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getPaperId, paperId);
        return getOne(wrapper);
    }

    /**
     * 获取学生参加的考试id
     * @param userId
     * @return
     */
    @Override
    public List<StudentPapers> getExamIdsByUserId(long userId) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId);
        return list(wrapper);
    }

    @Override
    public List<StudentPapers> getCompletedExam(long userId) {
        LambdaQueryWrapper<StudentPapers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentPapers::getStudentId, userId)
                .eq(StudentPapers::getStatus, 1)
                .ne(StudentPapers::getTotalScore, -1);
        return list(wrapper);
    }
}
