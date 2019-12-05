package com.junior.helpdesk.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.junior.helpdesk.api.dto.Summary;
import com.junior.helpdesk.api.entity.ChangeStatus;
import com.junior.helpdesk.api.entity.Ticket;
import com.junior.helpdesk.api.entity.User;
import com.junior.helpdesk.api.enums.ProfileEnum;
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
			
			if(ticketCurrentOptional.get().getAssignedUser() != null) {
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
	
	@GetMapping(value= "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id){
		Response<Ticket> response = new Response<>();
		try {
			if(StringUtils.isBlank(id)) {
				response.getErros().add("Paramter Id invalid.");
			    throw new Exception("Paramter Id invalid.");
			}
			
			Optional<Ticket> optional =  ticketService.findById(id);
			
			if(!optional.isPresent()) {
				response.getErros().add("Register not found by id:"+id);
				 throw new Exception("Register not found by id:"+id);
			}
			List<ChangeStatus> listaStatus = new ArrayList<ChangeStatus>();
			Iterable<ChangeStatus> listaStatusCurrent = ticketService.listChangeStatus(id);
			
			for (ChangeStatus changeStatus : listaStatusCurrent) {
				changeStatus.setTicket(null);
				listaStatus.add(changeStatus);
			}
			
			optional.get().setChanges(listaStatus);
			response.setData(optional.get());
			
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
		Response<String> response = new Response<>();
		try{
			
			if(StringUtils.isBlank(id)) {
				response.getErros().add("Paramter Id invalid.");
			    throw new Exception("Paramter Id invalid.");
			}
			
			Optional<Ticket> optional= ticketService.findById(id);
			
			if(!optional.isPresent()) {
				response.getErros().add("Register not found by id:"+id);
				 throw new Exception("Register not found by id:"+id);
			}
			
			ticketService.delete(id);
			
		}catch (Exception e) {
			response.getErros().add("Erro in method delete ticket.");
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, 
	@PathVariable("page") int page, @PathVariable("count") int count){
		Response<Page<Ticket>> response = new Response<>();
		Page<Ticket> tickets = null;
		
		try {
			User userRequest = userFromRequest(request);
			if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
				tickets = ticketService.listTicket(page, count);
			}else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
				tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
			}
			response.setData(tickets);
		}catch (Exception e) {
			response.getErros().add("Error in findAll method. " + e.getMessage());
			ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value="{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}") 
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request, 
			@PathVariable("page") int page, @PathVariable("count") int count,
			@PathVariable("number") Integer number, @PathVariable("title") String title,
			@PathVariable("status") String status, @PathVariable("priority") String priority,
			@PathVariable("assigned") boolean assigned){
		
		Response<Page<Ticket>> response = new Response<>();
		Page<Ticket> tickets = null;
		
		title = title.equals("uninformed") ? "" : title;
		status = status.equals("uninformed") ? "" : status;
		priority = priority.equals("uninformed") ? "" : priority;
		
		try {
			if(number > 0) {
				tickets = ticketService.findByNumber(page, count, number);
			}else {
				
				User userRequest = userFromRequest(request);
				
				if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
					if(assigned) {
						tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
					}else {
						tickets = ticketService.findByParameters(page, count, title, status, priority);
					}
				} else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
					tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
				}
			}
			response.setData(tickets);
		}catch (Exception e) {
			response.getErros().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(value="{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> changeStatus( 
			@PathVariable("id") String id,
			@PathVariable("status") String status, 
			HttpServletRequest request,
			@RequestBody Ticket ticket, 
			BindingResult result){
		
		Response<Ticket> response = new Response<>();
		
		try {
			this.validateChangeStatus(id, status, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			Optional<Ticket> current = ticketService.findById(id);
			
			if(!current.isPresent()) {
				response.getErros().add("No avaliable find ticket id:" + id);
				return ResponseEntity.badRequest().body(response);
			}
			
			current.get().setStatus(StatusEnum.getStatus(status));
			
			if(status.equals("Assigned")) {
				current.get().setAssignedUser(userFromRequest(request));
			}	
			Ticket ticketPersited = ticketService.createOrUpdate(current.get());
			ChangeStatus changeStatus = new ChangeStatus();
			changeStatus.setUserChange(userFromRequest(request));
			changeStatus.setDateChangeStatus(new Date());
			changeStatus.setStatus(StatusEnum.getStatus(status));
			changeStatus.setTicket(ticketPersited);
			ticketService.createChangeStatus(changeStatus);
			response.setData(ticketPersited);
			 
		}catch (Exception e) {
			response.getErros().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateChangeStatus(String id, String status, BindingResult result) {
		
		if(StringUtils.isBlank(id)) {
			result.addError(new ObjectError("Ticket", "Id no information."));
		}
		
		if(StringUtils.isBlank(status)) {
			result.addError(new ObjectError("Ticket","Status no information."));
		}
	}
	
	@GetMapping("/summary")
	public ResponseEntity<Response<Summary>> findSummary(){
		Response<Summary> response = new Response<>();
		Summary summary = new Summary();
		
		int amountNew = 0;
		int amountResolved = 0;
		int amountApproved = 0;
		int amountDisapproved = 0;
		int amountAssigned = 0;
		int amountClosed = 0;
		
		Iterable<Ticket> tickets = ticketService.findAll();
		
		if(tickets != null) {
			for (Ticket ticket : tickets) {
				
				if(ticket.getStatus().equals(StatusEnum.New)) {
					amountNew++;
				}
				if(ticket.getStatus().equals(StatusEnum.Approved)) {
					amountApproved++;
				}
				if(ticket.getStatus().equals(StatusEnum.Assigned)) {
					amountAssigned++;
				}
				if(ticket.getStatus().equals(StatusEnum.Closed)) {
					amountClosed++;
				}
				if(ticket.getStatus().equals(StatusEnum.Disapproved)) {
					amountDisapproved++;
				}
				if(ticket.getStatus().equals(StatusEnum.Resolved)) {
					amountResolved++;
				}
			}
		}
		summary.setAmountApproved(amountApproved);
		summary.setAmountAssigned(amountAssigned);
		summary.setAmountClosed(amountClosed);
		summary.setAmountDisapproved(amountDisapproved);
		summary.setAmountNew(amountNew);
		summary.setAmountResolved(amountResolved);
		
		response.setData(summary);
		return ResponseEntity.ok(response);
	}
}
