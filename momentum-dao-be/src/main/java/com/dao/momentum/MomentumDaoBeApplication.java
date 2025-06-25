package com.dao.momentum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MomentumDaoBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentumDaoBeApplication.class, args);
    }

}
