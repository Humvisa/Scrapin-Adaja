package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- 1. IMPORTA ESTO

@SpringBootApplication
@EnableScheduling // <-- 2. ACTIVA EL AUTOMATISMO AQUÍ 🔥
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}