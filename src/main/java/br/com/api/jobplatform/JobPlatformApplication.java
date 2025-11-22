package br.com.api.jobplatform;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class JobPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPlatformApplication.class, args);
	}

}
