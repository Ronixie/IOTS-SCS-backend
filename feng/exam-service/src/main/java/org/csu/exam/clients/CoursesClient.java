package org.csu.exam.clients;

import org.csu.config.FeignClientConfig;
import org.csu.exam.entity.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.csu.utils.Result;

import java.util.List;

@FeignClient(name = "other-service",configuration = FeignClientConfig.class)
public interface CoursesClient {
    @GetMapping("/courses/name/{courseId}")
    Result<String> getCourseName(@PathVariable("courseId") Long courseId);

    @GetMapping("/courses/students/{courseId}")
    Result<List<Long>> getStudentIdsByCourseId(@PathVariable("courseId") Long courseId);

    @GetMapping("/users/{uid}")
    Result<UserVO> getUserInfo(@PathVariable("uid") Long uid);

    @GetMapping("/users/courses/{uid}")
    Result<List<Long>> getCourseIdsByUid(@PathVariable("uid") Long uid);
}
