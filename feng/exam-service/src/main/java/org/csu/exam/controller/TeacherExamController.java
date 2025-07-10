package org.csu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.ExamResultVO;
import org.csu.exam.entity.vo.ExamStatsVO;
import org.csu.exam.entity.vo.StudentExamDetailVO;
import org.csu.exam.service.TeacherExamService;
import org.csu.utils.Result;
import org.csu.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师端考试管理控制器
 */
@RestController
@RequestMapping("/exams/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherExamController {
    
    private final TeacherExamService teacherExamService;

    /**
     * 获取课程下的考试列表
     */
    @Operation(summary = "获取课程考试列表")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/course/{courseId}")
    public Result<Map<String, Object>> getCourseExams(
            @PathVariable("courseId") Long courseId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        long teacherId = UserContext.getUser();
        log.info("获取课程考试列表, teacherId: {}, courseId: {}, status: {}, keyword: {}", 
                teacherId, courseId, status, keyword);
        return Result.success(teacherExamService.getCourseExams(teacherId, courseId, status, keyword, page, size));
    }

    /**
     * 获取考试的学生成绩列表
     */
    @Operation(summary = "获取考试学生成绩")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("{examId}/results")
    public Result<Map<String, Object>> getExamResults(
            @PathVariable("examId") String examId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", defaultValue = "score") String sortBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        long teacherId = UserContext.getUser();
        log.info("获取考试学生成绩, teacherId: {}, examId: {}, keyword: {}, sortBy: {}", 
                teacherId, examId, keyword, sortBy);
        return Result.success(teacherExamService.getExamResults(teacherId, examId, keyword, sortBy, page, size));
    }

    /**
     * 获取考试统计信息
     */
    @Operation(summary = "获取考试统计信息")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamStatsVO.class)))
    @GetMapping("{examId}/stats")
    public Result<ExamStatsVO> getExamStats(@PathVariable("examId") String examId) {
        long teacherId = UserContext.getUser();
        log.info("获取考试统计信息, teacherId: {}, examId: {}", teacherId, examId);
        return Result.success(teacherExamService.getExamStats(teacherId, examId));
    }

    /**
     * 获取学生考试详情
     */
    @Operation(summary = "获取学生考试详情")
    @Parameters({
            @Parameter(name = "examId", description = "考试ID", required = true),
            @Parameter(name = "studentId", description = "学生ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentExamDetailVO.class)))
    @GetMapping("{examId}/student/{studentId}/detail")
    public Result<StudentExamDetailVO> getStudentExamDetail(
            @PathVariable("examId") String examId,
            @PathVariable("studentId") Long studentId) {
        long teacherId = UserContext.getUser();
        log.info("获取学生考试详情, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);
        return Result.success(teacherExamService.getStudentExamDetail(teacherId, examId, studentId));
    }

    /**
     * 教师手动重新评分
     */
    @Operation(summary = "教师手动重新评分")
    @Parameters({
            @Parameter(name = "examId", description = "考试ID", required = true),
            @Parameter(name = "studentId", description = "学生ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("{examId}/student/{studentId}/manual-rescore")
    public Result<ExamResultVO> manualRescore(
            @PathVariable("examId") String examId,
            @PathVariable("studentId") Long studentId,
            @RequestBody Map<String, Object> requestBody) {
        long teacherId = UserContext.getUser();
        log.info("教师手动重新评分, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);
        return Result.success(teacherExamService.manualRescore(teacherId, examId, studentId, requestBody));
    }

    /**
     * 确认更新分数
     */
    @Operation(summary = "确认更新分数")
    @Parameters({
            @Parameter(name = "examId", description = "考试ID", required = true),
            @Parameter(name = "studentId", description = "学生ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("{examId}/student/{studentId}/confirm-score")
    public Result<String> confirmScore(
            @PathVariable("examId") String examId,
            @PathVariable("studentId") Long studentId) {
        long teacherId = UserContext.getUser();
        log.info("确认更新分数, teacherId: {}, examId: {}, studentId: {}", teacherId, examId, studentId);
        teacherExamService.confirmScore(teacherId, examId, studentId);
        return Result.success("分数更新成功");
    }

    /**
     * 导出考试成绩
     */
    @Operation(summary = "导出考试成绩")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("{examId}/export")
    public Result<String> exportExamResults(@PathVariable("examId") String examId) {
        long teacherId = UserContext.getUser();
        log.info("导出考试成绩, teacherId: {}, examId: {}", teacherId, examId);
        String downloadUrl = teacherExamService.exportExamResults(teacherId, examId);
        return Result.success(downloadUrl);
    }

    /**
     * 创建考试
     */
    @Operation(summary = "创建考试")
    @Parameter(name = "courseId", description = "课程ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/course/{courseId}/create")
    public Result<String> createExam(
            @PathVariable("courseId") Long courseId,
            @RequestBody Map<String, Object> requestBody) {
        long teacherId = UserContext.getUser();
        log.info("创建考试, teacherId: {}, courseId: {}", teacherId, courseId);
        return Result.success(teacherExamService.createExam(teacherId, courseId, requestBody));
    }

    /**
     * 编辑考试
     */
    @Operation(summary = "编辑考试")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("{examId}/edit")
    public Result<String> editExam(
            @PathVariable("examId") String examId,
            @RequestBody Map<String, Object> requestBody) {
        long teacherId = UserContext.getUser();
        log.info("编辑考试, teacherId: {}, examId: {}", teacherId, examId);
        return Result.success(teacherExamService.editExam(teacherId, examId, requestBody));
    }

    /**
     * 删除考试
     */
    @Operation(summary = "删除考试")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @DeleteMapping("{examId}")
    public Result<String> deleteExam(@PathVariable("examId") String examId) {
        long teacherId = UserContext.getUser();
        log.info("删除考试, teacherId: {}, examId: {}", teacherId, examId);
        teacherExamService.deleteExam(teacherId, examId);
        return Result.success("删除成功");
    }

    /**
     * 发布考试
     */
    @Operation(summary = "发布考试")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("/{examId}/publish")
    public Result<String> publishExam(@PathVariable("examId") String examId) {
        long teacherId = UserContext.getUser();
        log.info("发布考试, teacherId: {}, examId: {}", teacherId, examId);
        teacherExamService.publishExam(teacherId, examId);
        return Result.success("发布成功");
    }

    /**
     * 获取考试的题目
     */
    @Operation(summary = "获取考试的题目")
    @Parameter(name = "examId", description = "考试ID", required = true)
    @ApiResponse(responseCode = "200", description = "成功")
    @GetMapping("/{examId}/questions")
    public Result<List<TestPapers.Question>> getExamQuestions(@PathVariable("examId") String examId) {
        long teacherId = UserContext.getUser();
        log.info("获取考试的题目, teacherId: {}, examId: {}", teacherId, examId);
        return Result.success(teacherExamService.getExamQuestions(teacherId, examId));
    }
} 