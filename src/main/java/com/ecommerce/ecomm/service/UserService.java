package com.ecommerce.ecomm.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.ecomm.controller.RegisterRequest;
import com.ecommerce.ecomm.entities.PasswordResetToken;
import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
import com.ecommerce.ecomm.repository.PasswordResetTokenRepository;
import com.ecommerce.ecomm.repository.RoleRepository;
import com.ecommerce.ecomm.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public String getUsernameById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new EcommException(ErrorCode.USER_NOT_FOUND));
        return user.getUsername();
    }

    public void registerUser(RegisterRequest request) {
        Role role = roleRepository.findByName(request.getRole());
        if (role == null) {
            throw new EcommException(ErrorCode.ROLE_NOT_FOUND);
        }
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new EcommException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            throw new EcommException(ErrorCode.INVALID_REGISTRATION);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(new HashSet<>());
        user.getRoles().add(role);

        userRepository.save(user);

        logger.info("User registered with username: " + user.getUsername());
        logger.info("Assigned Role: " + role.getName());
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
            emailService.sendEmail(email, "Recuperação de senha", "Seu token de recuperação é: " + token);
        } else {
            logger.warn("Password reset attempt for non-existent email: " + email);
        }
    }

    public void changePassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken != null) {
            User user = passwordResetToken.getUser();
            Calendar cal = Calendar.getInstance();
            if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                throw new EcommException(ErrorCode.TOKEN_EXPIRED);
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            passwordResetTokenRepository.delete(passwordResetToken);
            logger.info("Password changed for user with ID: " + user.getId());
        } else {
            throw new EcommException(ErrorCode.INVALID_TOKEN);
        }
    }
}