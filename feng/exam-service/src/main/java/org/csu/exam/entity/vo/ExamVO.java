package org.csu.exam.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csu.exam.entity.po.Answer;
import org.csu.exam.entity.po.TestPapers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamVO {
    /**
     * 试卷信息
     */
    private TestPapers paper;
    /**
     * 答案列表
     */
    private List<Answer> answers;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 是否开始考试
     */
    private boolean start;
    /**
     * 是否提交试卷
     */
    private boolean submit;
    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 考试得分
     */
    private double totalScore=-1;
}
