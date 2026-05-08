package com.example;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.entity.Account;
import com.example.service.AccountService;

@SpringBootApplication
public class SpringDataJpaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AccountService accountService) {
		return args -> {
			accountService.getAllAccountsByCustomerId(1L)
					.forEach(account -> System.out.println(account.getNumber()));
		};
	}

}
