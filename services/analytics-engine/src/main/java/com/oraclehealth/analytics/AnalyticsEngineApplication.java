package com.oraclehealth.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AnalyticsEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsEngineApplication.class, args);
    }
}