package com.junior.helpdesk.api.respository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.junior.helpdesk.api.entity.ChangeStatus;

public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String>{

	Iterable<ChangeStatus> findByTicketIdOrderByDateChangeStatusDesc(String ticketId);
}
