package com.junior.helpdesk.api.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.helpdesk.api.entity.User;
import com.junior.helpdesk.api.response.Response;
import com.junior.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user,
			BindingResult result){
		Response<User> response = new Response<>();
		
		try {
			
			validateCreateUser(user, result);
				
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User userPersisted = userService.createOrUpdate(user);
			response.setData(userPersisted);
	
		}catch (DuplicateKeyException e) {
			response.getErros().add("Email already registration!");
			return ResponseEntity.badRequest().body(response);
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateCreateUser(User user, BindingResult result) {
		if(user != null && user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			result.addError(new ObjectError("User", "Email não informado"));
		}
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user,
			BindingResult result){
		Response<User> response = new Response<>();
		
		try {
			
			validateUpdateUser(user, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User persistedUser= userService.createOrUpdate(user);
			response.setData(persistedUser);
		}catch (Exception e) {
			ResponseEntity.badRequest().body(response);
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	private void validateUpdateUser(User user, BindingResult result) {
		
		if(user != null && user.getId() == null) {
			result.addError(new ObjectError("User", "Id não informado!"));
		}
		
		if(user != null && user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email não informado!"));
		}
		
	}
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> findById(@PathVariable("id") String id){
		try {
			Response<User> response = new Response<User>();
			Optional<User> user = userService.findById(id);
			
			if(!user.isPresent()) {
				response.getErros().add("User not found by id:" + id);
				return ResponseEntity.badRequest().body(response);
			}
			response.setData(user.get());
			
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response<User>());
		}
	}
	
	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
		
		try {
			Response<String> response = new Response<String>();
				
			Optional<User> user = userService.findById(id);
			
			if(!user.isPresent()) {
				response.getErros().add("User not found by id:" + id);
				return ResponseEntity.badRequest().body(response);
			}
			userService.delete(id);
			
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response<String>());
		}
	}
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<Page<User>>> findAll(@PathVariable("page") int page, @PathVariable("count") int count){
		
		try {
			Response<Page<User>> response = new Response<Page<User>>();
			Page<User> userPages = userService.findAll(page, count);
			response.setData(userPages);
			
			return ResponseEntity.ok(response);
		
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response<Page<User>>());
		}
		
	}
	
	
	
	
	
	
	
}
