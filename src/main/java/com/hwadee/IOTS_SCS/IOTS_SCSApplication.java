package com.hwadee.IOTS_SCS;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hwadee.IOTS_SCS")
@MapperScan(basePackages = "com.hwadee.IOTS_SCS.entity.mapper")
public class IOTS_SCSApplication {

    public static void main(String[] args) {
        SpringApplication.run(IOTS_SCSApplication.class, args);
    }

}
