package com.zwj.geminiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zwj.geminiservice.*")
public class GeminiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeminiServiceApplication.class, args);
    }

}
