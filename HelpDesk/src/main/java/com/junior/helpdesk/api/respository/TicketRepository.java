package com.junior.helpdesk.api.respository;

import java.awt.print.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.junior.helpdesk.api.entity.Ticket;

public interface TicketRepository extends MongoRepository<Ticket, String>{
	
	Page<Ticket> findByUserIdOrderByDateDesc(Pageable pages, String userId);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityIgnoreCaseContainingOrderByDateDesc(
			String title, String status, String priority, Pageable pages);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityIgnoreCaseContainingAndAssignedUserIdOrderByDateDesc(
			String title, String status, String priority, Pageable pages);
	
	Page<Ticket> findByNumber(Integer number, Pageable pages);
	
	
}