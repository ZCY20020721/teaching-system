package com.teaching.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.teaching.system.mapper")
public class TeachingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachingSystemApplication.class, args);
    }
}
