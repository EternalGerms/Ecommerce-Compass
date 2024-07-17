package com.ecommerce.ecomm.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

	private final String SECRET_KEY = "yourSecretKey";

	public String generateToken(String username, List<String> roles, List<String> permissions) {
		String token = Jwts.builder()
				.setSubject(username)
				.claim("roles", roles)
				.claim("permissions", permissions)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
				.compact();
		return "Bearer " + token;
	}
}