package com.tejusko.user_service.repository;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tejusko.user_service.entity.User;




	public interface UserRepository extends JpaRepository<User, Long> {
	    Optional<User> findByUsername(String username);
	}


