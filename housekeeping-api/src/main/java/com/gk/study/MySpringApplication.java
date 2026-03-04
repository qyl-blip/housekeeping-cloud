package com.gk.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MySpringApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(MySpringApplication.class, args);
        } catch (Exception e) {
            System.err.println(">>> 启动失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}