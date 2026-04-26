package com.example.optimization_algorithm_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OptimizationAlgorithmBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptimizationAlgorithmBackendApplication.class, args);
    }

}
