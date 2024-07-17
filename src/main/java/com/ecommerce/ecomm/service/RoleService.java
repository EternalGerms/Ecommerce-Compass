package com.ecommerce.ecomm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.entities.Permission;
import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.repository.PermissionRepository;
import com.ecommerce.ecomm.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	public void addPermissionToRole(String roleName, String permissionName) {
		Role role = roleRepository.findByName(roleName);
		Permission permission = permissionRepository.findByName(permissionName);
		if (role != null && permission != null) {
			role.getPermissions().add(permission);
			roleRepository.save(role);
		}
	}
}
