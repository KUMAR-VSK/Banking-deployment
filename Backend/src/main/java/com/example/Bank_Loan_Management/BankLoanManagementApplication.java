package com.example.Bank_Loan_Management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.Bank_Loan_Management.entity.User;
import com.example.Bank_Loan_Management.repository.UserRepository;
import com.example.Bank_Loan_Management.service.AuthService;

@SpringBootApplication
public class BankLoanManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankLoanManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, AuthService authService) {
		return args -> {
			// Create default admin user if it doesn't exist
			if (userRepository.findByUsername("testuser").isEmpty()) {
				try {
					authService.register("testuser", "testuser", "admin@example.com", User.Role.ADMIN);
					System.out.println("Default admin user created: testuser/testuser");
				} catch (Exception e) {
					System.err.println("Failed to create default admin user: " + e.getMessage());
				}
			}
		};
	}

}
