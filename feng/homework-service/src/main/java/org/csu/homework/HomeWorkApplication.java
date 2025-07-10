package org.csu.homework;


import org.csu.config.FeignClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.csu.homework", "org.csu"})
@EnableFeignClients(defaultConfiguration = FeignClientConfig.class)
public class HomeWorkApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeWorkApplication.class, args);
    }
}
