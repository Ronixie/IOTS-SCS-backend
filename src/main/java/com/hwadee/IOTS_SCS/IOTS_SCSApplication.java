package com.hwadee.IOTS_SCS;

import org.csu.config.FeignClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "com.hwadee.IOTS_SCS")
@MapperScan(basePackages = "com.hwadee.IOTS_SCS.mapper")
@ComponentScan(basePackages = {"com.hwadee.IOTS_SCS","org.csu.utils"})
@EnableFeignClients(defaultConfiguration = FeignClientConfig.class)
public class IOTS_SCSApplication {
    public static void main(String[] args) {
        SpringApplication.run(IOTS_SCSApplication.class, args);
    }
}
