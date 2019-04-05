package com.junior.firstspringboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstSpringBootController {
	
	@RequestMapping("/")
	public String home() {
		return "Welcome in home page";
	}
	
	@RequestMapping("/showText")
	public String showText() {
		return "Hello, first Spring Boot project";
	}
}
