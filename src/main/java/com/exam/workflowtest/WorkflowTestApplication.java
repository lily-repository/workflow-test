package com.exam.workflowtest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//禁用SpringBootSecurity问题
@SpringBootApplication(
        exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@MapperScan(
        basePackages = {"com.exam.workflowtest"})
public class WorkflowTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowTestApplication.class, args);
    }

}
