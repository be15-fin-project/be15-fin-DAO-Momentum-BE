package com.dao.momentum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.dao.momentum.notification.command.application.service")
public class MomentumDaoBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentumDaoBeApplication.class, args);
    }

}
