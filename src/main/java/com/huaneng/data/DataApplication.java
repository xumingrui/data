package com.huaneng.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.huaneng.swagger.annotation.EnableHNSwagger2;

/**
 * @Description: 启动类
 * @Author: xumingrui
 * @CreateDate: 2019/12/5 下午9:26
 */
// @EnableFeignClients(basePackages = "com.huaneng.gateway.feign")
@SpringBootApplication
@EnableScheduling
@EnableHNSwagger2
public class DataApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }
}
