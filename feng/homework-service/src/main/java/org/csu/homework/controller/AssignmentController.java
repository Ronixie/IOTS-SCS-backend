package org.csu.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.homework.entity.dto.AssignmentDTO;
import org.csu.homework.entity.dto.SubmissionScoreDTO;
import org.csu.homework.entity.po.Assignments;
import org.csu.homework.entity.vo.AssignmentAnalysisVO;
import org.csu.homework.entity.vo.AssignmentDetailVO;
import org.csu.homework.entity.vo.AssignmentsVO;
import org.csu.homework.entity.vo.EventTodoVO;
import org.csu.homework.service.IAssignmentsService;
import org.csu.utils.Result;
import org.csu.utils.UserContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {
    private final IAssignmentsService assignmentService;

    @Operation(summary = "获取作业列表")
    @ApiResponse(responseCode = "200", description = "获取作业列表成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AssignmentsVO.class)))
    @GetMapping
    public Result<Map<String, Object>> getAssignmentList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                         @RequestParam(value = "courseName", required = false) String courseName,
                                                         @RequestParam(value = "title", required = false) String title,
                                                         @RequestParam(value = "status", required = false) String status,
                                                         @RequestParam(value = "teacherName", required = false) String teacherName,
                                                         @RequestParam(value = "sort", required = false) String sort,
                                                         @RequestParam(value = "courseId", required = false) Long courseId) {
        long userId = UserContext.getUser();
        log.info("学生: {},获取作业列表", userId);
        Map<String, Object> res = assignmentService.getAssignmentList(userId, page, size, courseName, title, status, teacherName, sort, courseId);
        return Result.success(res);
    }

    @Operation(summary = "根据ID获取作业详情")
    @ApiResponse(responseCode = "200", description = "获取作业详情成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AssignmentDetailVO.class)))
    @GetMapping("/{id}")
    public Result<AssignmentDetailVO> getAssignmentById(@PathVariable long id) {
        long userId = UserContext.getUser();
        log.info("userId:{} 获取作业详情", userId);
        AssignmentDetailVO vo = assignmentService.getAssignmentById(id, userId);
        return Result.success(vo);
    }

    @Operation(summary = "下载作业附件")
    @ApiResponse(responseCode = "200", description = "下载作业附件成功",
            content = @Content(mediaType = "application/octet-stream"))
    @GetMapping("/{id}/download/{tag}")
    public ResponseEntity<InputStreamResource> download(@PathVariable long id, @PathVariable String tag, @RequestParam("attachment") String attachment, @RequestParam(value = "submissionId", required = false) Long submissionId) {
        long userId = UserContext.getUser();
        log.info("userId:{} 下载作业附件", userId);
        if (submissionId == null) submissionId = 0L;
        File file = assignmentService.downloadAttachment(id, userId, tag, attachment, submissionId);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            log.error("下载附件失败", e);
            throw new RuntimeException("下载附件失败");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8)
                .replace("+", "%20"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @Operation(summary = "提交作业")
    @ApiResponse(responseCode = "200", description = "提交作业成功")
    @PostMapping("/{id}")
    public Result<String> submitAssignment(@PathVariable long id, @RequestParam("content") String content, @RequestParam("files") MultipartFile[] files) {
        long userId = UserContext.getUser();
        log.info("userId:{} 提交作业", userId);
        assignmentService.submitAssignment(id, userId, content, files);
        return Result.success("提交成功");
    }

    @Operation(summary = "获取教师作业列表")
    @ApiResponse(responseCode = "200", description = "获取教师作业列表成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    @GetMapping("/teacher")
    public Result<Map<String, Object>> getTeacherAssignmentList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                                @RequestParam(value = "courseName", required = false) String courseName,
                                                                @RequestParam(value = "title", required = false) String title,
                                                                @RequestParam(value = "courseId", required = false) Long courseId,
                                                                @RequestParam(value = "status", required = false) String status) {
        long userId = UserContext.getUser();
        log.info("教师: {},获取作业列表", userId);
        Map<String, Object> res = assignmentService.getTeacherAssignmentList(userId, page, size, courseName, title, courseId, status);
        return Result.success(res);
    }

    @Operation(summary = "更新作业")
    @ApiResponse(responseCode = "200", description = "更新作业成功")
    @PutMapping("/teacher/{id}")
    public Result<String> updateAssignment(@PathVariable long id, @RequestPart("dto") AssignmentDTO dto, @RequestPart(value = "files", required = false) MultipartFile[] files) {
        long userId = UserContext.getUser();
        log.info("教师: {},更新作业:{}", userId, id);
        assignmentService.updateAssignment(id, userId, dto, files);
        return Result.success("更新成功");
    }

    @Operation(summary = "创建作业")
    @ApiResponse(responseCode = "200", description = "创建作业成功")
    @PostMapping("/teacher")
    public Result<String> createAssignment(@RequestPart("dto") Assignments dto, @RequestPart(value = "files", required = false) MultipartFile[] files) {
        long userId = UserContext.getUser();
        log.info("教师: {},创建作业", userId);
        assignmentService.createAssignment(userId, dto, files);
        return Result.success("创建成功");
    }

    @Operation(summary = "删除作业")
    @ApiResponse(responseCode = "200", description = "删除作业成功")
    @DeleteMapping("/teacher/{id}")
    public Result<String> deleteAssignment(@PathVariable long id) {
        long userId = UserContext.getUser();
        log.info("教师: {},删除作业:{}", userId, id);
        assignmentService.deleteAssignment(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取作业提交列表")
    @ApiResponse(responseCode = "200", description = "获取作业提交列表成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    @GetMapping("/teacher/submissions/{id}")
    public Result<Map<String, Object>> getSubmissions(@PathVariable long id, @RequestParam("page") int page, @RequestParam("size") int size,
                                                      @RequestParam(value = "status", required = false) String status,
                                                      @RequestParam(value = "studentKeyWord", required = false) String studentKeyWord) {
        long userId = UserContext.getUser();
        log.info("教师: {},获取作业: {} 的提交列表", userId, id);
        Map<String, Object> res = assignmentService.getTeacherSubmissions(id, userId, page, size, status, studentKeyWord);
        return Result.success(res);
    }

    @Operation(summary = "批改作业")
    @ApiResponse(responseCode = "200", description = "批改作业成功")
    @PutMapping("/teacher/grade/{id}")
    public Result<String> updateSubmission(@PathVariable long id, @RequestBody SubmissionScoreDTO dto) {
        long userId = UserContext.getUser();
        log.info("教师: {},批改作业: {}", userId, id);
        assignmentService.scoreSubmission(id, userId, dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "拒绝作业提交")
    @ApiResponse(responseCode = "200", description = "拒绝作业提交成功")
    @PutMapping("/teacher/submissions/reject/{id}")
    public Result<String> rejectSubmission(@PathVariable long id) {
        long userId = UserContext.getUser();
        log.info("教师: {},拒绝作业: {}", userId, id);
        assignmentService.rejectSubmission(id, userId);
        return Result.success("更新成功");
    }

    @Operation(summary = "内部调用接口，获取作业信息进行分析")
    @GetMapping("/analysis")
    public Result<List<AssignmentAnalysisVO>> getAssignmentAnalysis(@RequestParam("courseId") Long courseId) {
        long userId = UserContext.getUser();
        List<AssignmentAnalysisVO> res = assignmentService.getAssignmentAnalysis(userId,courseId);
        return Result.success(res);
    }


    @Operation(summary = "获取待办事项")
    @GetMapping("/todo")
    public Result<List<EventTodoVO>> getTodoList() {
        long userId = UserContext.getUser();
        List<EventTodoVO> res = assignmentService.getTodoList(userId);
        return Result.success(res);
    }
}