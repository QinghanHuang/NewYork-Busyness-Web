package com.clement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FrameWorkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameWorkBackendApplication.class, args);
    }

}
