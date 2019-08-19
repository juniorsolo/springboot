package com.junior.helpdesk.api.controller;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.helpdesk.api.entity.Ticket;
import com.junior.helpdesk.api.entity.User;
import com.junior.helpdesk.api.enums.StatusEnum;
import com.junior.helpdesk.api.response.Response;
import com.junior.helpdesk.api.security.jwt.JwtTokenUtil;
import com.junior.helpdesk.api.service.TicketService;
import com.junior.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

	@Autowired
	private TicketService ticketService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result) {
		Response<Ticket> response = new Response<Ticket>();

		try {
			validateCreateTicket(ticket, result);

			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(StatusEnum.New);
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
			response.setData(ticketPersisted);

		} catch (Exception e) {
			response.getErros().add("Error create ticket." + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	private void validateCreateTicket(Ticket ticket, BindingResult result) {

		if (ticket == null) {
			result.addError(new ObjectError("Ticket", "Ticket nulo."));
			return;
		}

		if (StringUtils.isBlank(ticket.getTitle())) {
			result.addError(new ObjectError("Ticket", "Title not informed"));

		}
	}

	private User userFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.getUsernameFromToken(token);
		return userService.findByEmail(email);
	}

	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result){
		Response<Ticket> response = new Response<>();
		try {
			
			this.validateUpdateTicket(ticket, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			Optional<Ticket> ticketCurrentOptional = ticketService.findById(ticket.getId());
			
			if(!ticketCurrentOptional.isPresent()) {
				response.getErros().add( "Not possible find ticket by id.");
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(ticketCurrentOptional.get().getStatus());
			ticket.setUser(ticketCurrentOptional.get().getUser());
			ticket.setDate(ticketCurrentOptional.get().getDate());
			ticket.setNumber(ticketCurrentOptional.get().getNumber());
			
			if(ticketCurrentOptional.get().getAssignedUser() == null) {
				ticket.setAssignedUser(ticketCurrentOptional.get().getAssignedUser());
			}
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErros().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	private void validateUpdateTicket(Ticket ticket, BindingResult result) {
		
		if(ticket == null) {
			result.addError(new ObjectError("Ticket", "Object ticket is null."));
			return;
		}else if(StringUtils.isBlank(ticket.getId())) {
			result.addError(new ObjectError("Ticket", "Id's ticket is null."));
		}else if(StringUtils.isBlank(ticket.getTitle())) {
			result.addError(new ObjectError("Ticket", "Title ticket is null."));
		}
			
	}
}
