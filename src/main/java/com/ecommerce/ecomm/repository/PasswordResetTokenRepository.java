package com.ecommerce.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecomm.entities.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findByToken(String token);
}