package com.ecommerce.ecomm.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

    private final String SECRET_KEY = "yourSecretKey";

    public String generateToken(String username, Object roles, Object permissions) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                .compact();
        return "Bearer " + token;
    }
}