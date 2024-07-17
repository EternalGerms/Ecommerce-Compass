package com.ecommerce.ecomm.service;

import com.ecommerce.ecomm.entities.PasswordResetToken;
import com.ecommerce.ecomm.entities.User;
import com.ecommerce.ecomm.exception.EcommException;
import com.ecommerce.ecomm.exception.ErrorCode;
import com.ecommerce.ecomm.repository.PasswordResetTokenRepository;
import com.ecommerce.ecomm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
        	throw new EcommException(ErrorCode.PRODUTO_INATIVO);
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
        String resetUrl = token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", resetUrl);
    }
    
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || isTokenExpired(resetToken)) {
            throw new EcommException(ErrorCode.INVALID_TOKEN);
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    private boolean isTokenExpired(PasswordResetToken token) {
        Calendar cal = Calendar.getInstance();
        return token.getExpiryDate().before(cal.getTime());
    }
    
}