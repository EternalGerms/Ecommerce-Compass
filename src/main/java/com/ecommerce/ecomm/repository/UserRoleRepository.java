package com.ecommerce.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecomm.entities.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}