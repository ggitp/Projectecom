package com.shop.ecommerce.service;

import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> authenticateUser(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                result.put("success", true);
                result.put("message", "Login successful");
                result.put("user", user);
            } else {
                result.put("success", false);
                result.put("message", "Invalid password");
            }
        } else {
            result.put("success", false);
            result.put("message", "User not found");
        }
        
        return result;
    }

    public boolean isValidCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPasswordHash());
    }
} 