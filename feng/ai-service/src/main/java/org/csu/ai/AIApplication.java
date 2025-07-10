package org.csu.ai;


import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.csu.config.FeignClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = FeignClientConfig.class)
@ComponentScan(basePackages = {"org.csu.ai", "org.csu"})
@MapperScan("org.csu.ai.mapper")
@EnableMPP
@EnableTransactionManagement
public class AIApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
    }
}
