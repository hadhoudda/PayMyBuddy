package com.paymybuddy;

import com.paymybuddy.model.User;
import com.paymybuddy.service.CompteService;
import com.paymybuddy.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class PaymybuddyApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private CompteService compteService;

	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
	}
}
