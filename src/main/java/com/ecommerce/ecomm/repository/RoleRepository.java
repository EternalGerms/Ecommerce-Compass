package com.ecommerce.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecomm.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}