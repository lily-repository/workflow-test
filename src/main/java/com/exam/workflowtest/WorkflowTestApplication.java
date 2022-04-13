package com.exam.workflowtest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.exam.workflowtest"})
public class WorkflowTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowTestApplication.class, args);
    }

}
