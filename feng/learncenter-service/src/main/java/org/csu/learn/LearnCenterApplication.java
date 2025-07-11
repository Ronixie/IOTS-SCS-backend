package org.csu.learn;

import org.csu.config.FeignClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.csu.learn", "org.csu"})
@EnableFeignClients(defaultConfiguration = FeignClientConfig.class)
public class LearnCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearnCenterApplication.class, args);
    }
}
