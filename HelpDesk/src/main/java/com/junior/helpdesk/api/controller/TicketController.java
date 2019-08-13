package com.junior.helpdesk.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junior.helpdesk.api.entity.Ticket;
import com.junior.helpdesk.api.response.Response;
import com.junior.helpdesk.api.security.jwt.JwtTokenUtil;
import com.junior.helpdesk.api.service.TicketService;
import com.junior.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin( origins =  "*")
public class TicketController {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result){
	    Response<Ticket> response = new Response<Ticket>();
	    
	    try {
	    	validateCreateTicket(ticket, result);
	    	
	    	if(result.hasErrors()) {
	    		result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
	    		return ResponseEntity.badRequest().body(response);
	    	}
	    	
	    }catch (Exception e) {
	    	response.getErros().add("Error create ticket."+ e.getMessage());
			return ResponseEntity.badRequest().body(response);	
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		
		if(ticket == null ) {
			result.addError(new ObjectError("Ticket","Ticket nulo."));
			return ;
		}
		
		if( StringUtils.isBlank(ticket.getTitle())) {
			result.addError(new ObjectError("Ticket", "Title not informed"));
			
		}
	}
}
