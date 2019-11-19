package com.junior.helpdesk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.junior.helpdesk.api.entity.User;
import com.junior.helpdesk.api.enums.ProfileEnum;
import com.junior.helpdesk.api.respository.UserRepository;

@SpringBootApplication
public class HelpDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelpDeskApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			initUsers(userRepository, passwordEncoder);
		};
	}
	
	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		User admin = new User();
		admin.setEmail("admin@helpdesk.com");
		admin.setPassword(passwordEncoder.encode("123"));
		admin.setProfile(ProfileEnum.ROLE_ADMIN);
		
		User technician = new User();
		technician.setEmail("technician@test.com");
		technician.setPassword(passwordEncoder.encode("123"));
		technician.setProfile(ProfileEnum.ROLE_TECHNICIAN);
		
		User customer = new User();
		customer.setEmail("customer@test.com");
		customer.setPassword(passwordEncoder.encode("123"));
		customer.setProfile(ProfileEnum.ROLE_CUSTOMER);
		
		User find = userRepository.findByEmail("admin@helpdesk.com");
		if(find == null) {
			userRepository.save(admin);
			userRepository.save(technician);
			userRepository.save(customer);
		}
	}
	

	
}

