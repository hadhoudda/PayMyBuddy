package com.paymybuddy;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class PaymybuddyApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(PaymybuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		Iterable<User> users = userService.getUsers();
//		users.forEach(user -> System.out.println(user.getLastName()));

		Optional<User> optionalUser = userService.getUserById(1);
		User user1 = optionalUser.get();

		//user1.getComptes().forEach(compte -> System.out.println("le solde de user1 est " + compte.getSolde()));
	}
}
