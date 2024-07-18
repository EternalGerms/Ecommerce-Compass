package com.ecommerce.ecomm.service;

import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    @Autowired
    private UserRepository userRepository;

    public boolean checkUserPermissions(User user, String permission) {
        logger.debug("Checking permissions for user: {}, permission: {}", user.getUsername(), permission);
        boolean hasPermission = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(perm -> perm.getName().equals(permission));
        logger.debug("User {} has permission {}: {}", user.getUsername(), permission, hasPermission);
        return hasPermission;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName());
    }
}