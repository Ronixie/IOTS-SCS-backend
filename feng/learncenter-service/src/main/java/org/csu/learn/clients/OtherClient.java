package org.csu.learn.clients;

import org.csu.config.FeignClientConfig;
import org.csu.learn.entity.vo.UserVO;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "other-service",configuration = FeignClientConfig.class)
public interface OtherClient {
    @GetMapping("/users/{uid}")
    Result<UserVO> getUserInfo(@PathVariable("uid") Long uid);

    @GetMapping("/users/courses/{uid}")
    Result<List<Long>> getCourseIdsByUid(@PathVariable("uid") Long uid);

    @GetMapping("/courses/name/{courseId}")
    Result<String> getCourseName(@PathVariable("courseId") Long courseId);
}
