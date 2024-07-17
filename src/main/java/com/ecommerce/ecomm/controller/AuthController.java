package com.ecommerce.ecomm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecomm.entities.Permission;
import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.repository.UserRepository;
import com.ecommerce.ecomm.service.AuthService;
import com.ecommerce.ecomm.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && userService.checkPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
			List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
			List<String> permissions = user.getRoles().stream().flatMap(role -> role.getPermissions().stream())
					.map(Permission::getName).collect(Collectors.toList());

			// Add logs for debugging
			System.out.println("Login Request - Username: " + loginRequest.getUsername());
			System.out.println("Login Request - Roles: " + roles);
			System.out.println("Login Request - Permissions: " + permissions);

			String token = authService.generateToken(loginRequest.getUsername(), roles, permissions);
			return ResponseEntity.ok(token);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
		userService.registerUser(registerRequest);
		return ResponseEntity.ok("User registered successfully.");
	}
}