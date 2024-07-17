package com.ecommerce.ecomm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@PostMapping("/add-permission")
	public ResponseEntity<String> addPermissionToRole(@RequestBody AddPermissionRequest request) {
		roleService.addPermissionToRole(request.getRoleName(), request.getPermissionName());
		return ResponseEntity.ok("Permission added to role successfully.");
	}
}