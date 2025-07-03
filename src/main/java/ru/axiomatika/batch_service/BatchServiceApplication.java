package ru.axiomatika.batch_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ru.axiomatika.batch_service.web.mappers", "ru.axiomatika.batch_service"})
@EnableFeignClients(basePackages = "ru.axiomatika.batch_service.core.request")
public class BatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchServiceApplication.class, args);
	}

}
