package br.com.api.workTree;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class WorkTreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkTreeApplication.class, args);
	}

}
