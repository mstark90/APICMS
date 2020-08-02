package com.starkindustriesne.apicms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ApicmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApicmsApplication.class, args);
    }

}
