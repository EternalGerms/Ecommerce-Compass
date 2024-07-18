package com.ecommerce.ecomm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}