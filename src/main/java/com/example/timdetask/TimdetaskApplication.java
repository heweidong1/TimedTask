package com.example.timdetask;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.**.mapper")
public class TimdetaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimdetaskApplication.class, args);
    }

}
