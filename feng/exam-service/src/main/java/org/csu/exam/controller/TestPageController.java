package org.csu.exam.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.exam.clients.AIClient;
import org.csu.exam.entity.po.Answer;
import org.csu.exam.entity.po.TestPapers;
import org.csu.exam.entity.vo.EventTodoVO;
import org.csu.exam.entity.vo.ExamAnalysisVO;
import org.csu.exam.entity.vo.ExamListVO;
import org.csu.exam.entity.vo.ExamVO;
import org.csu.exam.service.TestPapersService;
import org.csu.utils.Result;
import org.csu.utils.TokenTools;
import org.csu.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 试卷相关接口
 */
@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Slf4j
public class TestPageController {
    private final TestPapersService testPapersService;
    private final AIClient aiClient;

    /**
     * 根据试卷ID获取试卷信息
     *
     * @param id 试卷ID
     * @return 包含试卷信息的Result对象
     */
    @Operation(summary = "根据Id获取试卷")
    @Parameter(name = "id", description = "试卷id", required = true)
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamVO.class)))
    @GetMapping("/{id}")
    public Result<ExamVO> testPage(@PathVariable("id") String id) {
        log.info("获取试卷ID: {}", id);
        long userId = UserContext.getUser();
        return Result.success(testPapersService.getTestPapersById(id, userId));
    }

    /**
     * 提交试卷
     *
     * @param id 试卷ID
     * @return Result对象
     */
    @Operation(summary = "提交试卷")
    @Parameter(name = "id", description = "试卷id", required = true)
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @PostMapping("/{id}")
    public Result<String> submitPaper(@PathVariable("id") String id) {
        log.info("提交试卷ID: {}", id);
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        // 更新状态
        testPapersService.submitPaper(id, userId);
        return Result.success();
    }

    @Operation(summary = "提交答案")
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @PostMapping("/{id}/answer")
    public Result<String> answer(@RequestBody Answer answer, @PathVariable("id") String id) {
        log.info("提交答案, 试卷ID: {}, 答案: {}", id, answer);
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        answer.setPaperId(id);
        // 保存答案
        boolean result = testPapersService.saveOrUpdateQuestionAnswer(answer, userId);
        if (!result) {
            log.error("保存答案失败, 试卷ID: {}, 用户ID: {}", id, userId);
            return Result.error();
        }
        return Result.success();
    }

    /**
     * 进行AI评分
     *
     * @param id 试卷ID
     * @return 结果
     */
    @Operation(summary = "AI评分")
    @Parameters({
            @Parameter(name = "id", description = "试卷id", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamVO.class)))
    @GetMapping("/{id}/ai-score")
    public Result<ExamVO> ai(@PathVariable("id") String id) {
        log.info("AI评分, 试卷ID: {}", id);
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        // 获取试卷和学生答案
        ExamVO paper = testPapersService.getTestPapersById(id, userId);
        String prompt = JSONObject.toJSONString(paper);
        String res = aiClient.aiToScore(prompt);

        Pattern pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(res);
        if (matcher.find()) { // 判断是否匹配
            // group(1) 获取第一个分组（即 ```json 和 ``` 之间的内容）
            String jsonContent = matcher.group(1).trim(); // trim() 去除首尾空格
            ExamVO vo = JSONObject.parseObject(jsonContent, ExamVO.class);
            // 计入学生总分
            Thread t = new Thread(() -> testPapersService.updateScore(userId, id, vo.getTotalScore(), vo.getAnswers()));
            t.start();
            return Result.success(vo);
        } else {
            log.error("AI评分失败, 试卷ID: {}, 响应内容: {}", id, res);
            return Result.error("AI评分失败");
        }
    }

    /**
     * 获取试卷上的问题
     *
     * @param id
     * @param questionId
     * @return
     */
    @Operation(summary = "获取问题")
    @Parameters({
            @Parameter(name = "id", description = "试卷id", required = true),
            @Parameter(name = "questionId", description = "问题id", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestPapers.Question.class)))
    @GetMapping("/{id}/{questionId}")
    public Result<Object> getQuestion(@PathVariable("id") String id, @PathVariable("questionId") String questionId) {
        log.info("获取问题, 试卷ID: {}, 问题ID: {}", id, questionId);
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        return Result.success(testPapersService.getQuestionById(id, questionId, userId));
    }

    @Operation(summary = "获取试卷列表")
    @Parameter(name = "id", description = "试卷id", required = true)
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamListVO.class)))
    @GetMapping
    public Result<Map<String, Object>> getExamList(@RequestParam("page") int page, @RequestParam("size") int size,
                                                   @RequestParam(value = "courseName",required = false) String courseName,
                                                   @RequestParam(value = "status",required = false) String status,
                                                   @RequestParam(value = "title",required = false) String title,
                                                   @RequestParam(value = "courseId",required = false) Long courseId) {
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        log.info("{}:获取试卷列表  page:{},size:{},courseId:{},courseName:{},status:{},title:{}", userId, page, size,courseId,courseName,status,title);
        return Result.success(testPapersService.getExamList(userId, page, size,courseName,status,title,courseId));
    }


    @Operation(summary = "预约考试")
    @Parameters({
            @Parameter(name = "id", description = "试卷id", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    @PostMapping("/{id}/reservation")
    public Result<String> reservation(@PathVariable("id") String id) {
        log.info("预约考试, 试卷ID: {}", id);
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        // 更新状态
        testPapersService.reservationExam(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取可预约的考试")
    @Parameters({
            @Parameter(name = "courseId", description = "课程id", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamListVO.class)))
    @GetMapping("/reservation/{courseId}")
    public Result<List<ExamListVO>> getReservationExamList(@PathVariable("courseId") Long courseId) {
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        log.info("{}:获取可预约试卷列表:courseId:{}", userId,courseId);
        return Result.success(testPapersService.getReservationExamList(userId,courseId));
    }
    @Operation(summary = "内部调用接口，获取信息进行分析")
    @GetMapping("/analysis")
    public Result<List<ExamAnalysisVO>> forAnalysis(@RequestParam("courseId") Long courseId) {
        long userId = UserContext.getUser();
        return Result.success(testPapersService.getAnalysisInfo(userId,courseId));
    }

    @Operation(summary = "获取待办考试")
    @ApiResponse(responseCode = "200", description = "成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventTodoVO.class)))
    @GetMapping("/todo")
    public Result<List<EventTodoVO>> getTodoExamList() {
        // 获取用户(学生)id
        long userId = UserContext.getUser();
        log.info("{}:获取待办试卷列表", userId);
        return Result.success(testPapersService.getTodoExamList(userId));
    }
    private final TokenTools tokenTools;

    @GetMapping("/temp")
    public String temp() {
        log.info("生成临时token");
        return tokenTools.createShortToken(123456L);
    }
}