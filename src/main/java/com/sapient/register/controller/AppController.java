package com.sapient.register.controller;

import java.util.Collections;
import java.util.List;

import com.sapient.register.model.User;
import com.sapient.register.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class AppController {

	@Autowired
	private WebClient webClient;

	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepo.save(user);
		
		return "register_success";
	}
	
	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> listUsers = userRepo.findAll();
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}

	@GetMapping("/api/users")
	public String users(Model model){

		String returnValue = null;
		List<User> listUsers = null;
		try {
			listUsers = this.webClient
					.get()
					.uri("http://34.28.252.10:80/users")
					.retrieve()
					.bodyToMono(List.class)
					.block();
			returnValue = "users";
		} catch(Exception e) {
			returnValue = "autherror";
		}
		model.addAttribute("listUsers", listUsers);

		return returnValue;
	}

	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<Object> getListOfUsers() {
		String returnValue = null;

		List<User>	listUsers = this.webClient
					.get()
					.uri("http://34.28.252.10:80/users")
					.retrieve()
					.bodyToMono(List.class)
					.block();

		return new ResponseEntity<>(listUsers, HttpStatus.OK);
	}

	@GetMapping("/send2faCode")
	public String twofa(){

		WebClient.create("http://localhost:8082")
				.put()
				.uri(uriBuilder -> uriBuilder
						.path("/users/{userId}/emails/{emailId}/2fa")
						.build(1, "shilpamurthy88@gmail.com"))
				.retrieve()
				.bodyToMono(Void.class);

		return "2faPage";
	}

	@PostMapping("/process_2fa")
	public String processTwoFA(String otp) {



		return "register_success";
	}
}
