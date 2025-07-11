package org.csu.exam;


import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.csu.config.FeignClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = FeignClientConfig.class)
@ComponentScan(basePackages = {"org.csu.exam", "org.csu"})
@MapperScan("org.csu.exam.mapper")
@EnableMPP // 允许复合主键
@EnableCaching
@EnableTransactionManagement // 开启事务
public class ExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }
}
