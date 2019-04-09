package com.junior.helpdesk.respository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.junior.helpdesk.api.entity.User;

public interface UserRepository extends MongoRepository<User, String>{
	User findByEmail(String email);
}
