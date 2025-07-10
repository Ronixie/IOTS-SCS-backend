package org.csu.learn.clients;


import org.csu.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service",configuration = FeignClientConfig.class)
public interface UserClient {

}
