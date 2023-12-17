package com.zwj.wenxinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zwj.wenxinservice.*")
public class WenXinServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WenXinServiceApplication.class, args);
	}

}
