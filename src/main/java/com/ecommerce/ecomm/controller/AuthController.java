package com.ecommerce.ecomm.controller;

import com.ecommerce.ecomm.dto.LoginResponse;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
import com.ecommerce.ecomm.repository.UserRepository;
import com.ecommerce.ecomm.service.AuthService;
import com.ecommerce.ecomm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && userService.checkPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
			String token = authService.generateToken(loginRequest.getUsername(), null, null);
			return ResponseEntity.ok(new LoginResponse(token));
		} else {
			throw new EcommException(ErrorCode.INVALID_LOGIN);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegisterRequest registerRequest) {
		userService.registerUser(registerRequest);
		return ResponseEntity.ok(createSuccessResponse("Usuário registrado com sucesso."));
	}

	private Map<String, String> createSuccessResponse(String message) {
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		return response;
	}
}