package com.oraclehealth.clinical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ClinicalDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalDataServiceApplication.class, args);
    }
}