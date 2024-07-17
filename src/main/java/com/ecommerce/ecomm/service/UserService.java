package com.ecommerce.ecomm.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.controller.RegisterRequest;
import com.ecommerce.ecomm.entities.PasswordResetToken;
import com.ecommerce.ecomm.entities.Permission;
import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.repository.PasswordResetTokenRepository;
import com.ecommerce.ecomm.repository.PermissionRepository;
import com.ecommerce.ecomm.repository.RoleRepository;
import com.ecommerce.ecomm.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private EmailService emailService;

	public void registerUser(RegisterRequest request) {
		Role role = roleRepository.findByName(request.getRole());
		if (role == null) {
			throw new RuntimeException("Role not found: " + request.getRole());
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setRoles(new HashSet<>());
		user.getRoles().add(role);

		// Assign permissions
		if (request.getPermissions() != null) {
			for (String permissionName : request.getPermissions().split(",")) {
				Permission permission = permissionRepository.findByName(permissionName);
				if (permission != null) {
					role.getPermissions().add(permission);
				}
			}
		}

		// Save user with roles and permissions
		userRepository.save(user);

		// Adicione logs para depuração
		System.out.println("User registered: " + user.getUsername());
		System.out.println("Assigned Role: " + role.getName());
		if (request.getPermissions() != null) {
			for (String permissionName : request.getPermissions().split(",")) {
				System.out.println("Assigned Permission: " + permissionName);
			}
		}
	}

	public boolean checkPassword(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return passwordEncoder.matches(password, user.getPassword());
		}
		return false;
	}

	public void resetPassword(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			String token = UUID.randomUUID().toString();
			PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
			passwordResetTokenRepository.save(passwordResetToken);
			emailService.sendEmail(email, "Password Reset", "Your password reset token is: " + token);
		}
	}

	public void changePassword(String token, String newPassword) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
		if (passwordResetToken != null) {
			User user = passwordResetToken.getUser();
			Calendar cal = Calendar.getInstance();
			if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
				throw new RuntimeException("Token expired");
			}
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			passwordResetTokenRepository.delete(passwordResetToken);
		} else {
			throw new RuntimeException("Invalid token");
		}
	}
}