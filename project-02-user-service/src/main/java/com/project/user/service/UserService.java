package com.project.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.user.entity.User;
import com.project.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    // Create user
    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Check whether an account exists
    public boolean accountExists(String identifier) {
        return userRepository.existsByEmail(identifier)
                || userRepository.existsByPhone(identifier);
    }

    // Generate OTP for password reset
    public String requestPasswordReset(String identifier) {

        if (!accountExists(identifier)) {
            throw new RuntimeException("Account not found");
        }

        otpService.createOtp(identifier);

        return "OTP generated successfully";
    }
 // Verify password reset OTP and generate reset token
    public String verifyOtp(String identifier, String otp) {

        return otpService.verifyOtp(identifier, otp);

    }
 // Reset password using verified reset token
    public boolean resetPassword(
            String identifier,
            String resetToken,
            String newPassword) {

        if (identifier == null || identifier.isBlank()
                || resetToken == null || resetToken.isBlank()
                || newPassword == null || newPassword.isBlank()) {

            return false;
        }

        if (!accountExists(identifier)) {
            return false;
        }

        var verification =
                otpService.validateResetToken(
                        identifier,
                        resetToken
                );

        if (verification == null) {
            return false;
        }

        User user;

        if (userRepository.existsByEmail(identifier)) {
            user = getUserByEmail(identifier);
        } else {
            user = userRepository.findByPhone(identifier)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        return true;
    }
    public boolean changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        if (email == null || email.isBlank()
                || currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {
            return false;
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        return true;
    }
    // Update user
    public User updateUser(Long id, User user) {

        User existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null
                && !user.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());

        return userRepository.save(existingUser);
    }
}