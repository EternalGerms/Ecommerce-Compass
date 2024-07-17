package com.ecommerce.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecomm.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByEmail(String email);
}