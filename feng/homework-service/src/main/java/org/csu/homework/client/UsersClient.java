package org.csu.homework.client;

import org.csu.config.FeignClientConfig;
import org.csu.homework.entity.vo.UserVO;
import org.csu.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "other-service",configuration = FeignClientConfig.class)
public interface UsersClient {

}
