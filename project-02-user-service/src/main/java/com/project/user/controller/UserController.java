package com.project.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.project.user.dto.LoginRequest;
import com.project.user.dto.LoginResponse;
import com.project.user.entity.User;
import com.project.user.security.JwtUtil;
import com.project.user.service.LoginAttemptService;
import com.project.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public UserController(
            UserService userService,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService) {

        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    // Create user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    // Update user
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return userService.updateUser(id, user);
    }

    // Login and generate JWT token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            String email = request.getEmail();

            if (email == null || email.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            if (loginAttemptService.isBlocked(email)) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Account temporarily blocked. Try again later.");
            }

            User user = userService.getUserByEmail(email);

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {

                loginAttemptService.recordFailedAttempt(email);

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // Successful login
            loginAttemptService.resetAttempts(email);

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(
                    new LoginResponse(
                            user.getId(),
                            token,
                            user.getEmail(),
                            user.getRole()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    // Check whether an account exists
    @PostMapping("/verify-account")
    public Map<String, Boolean> verifyAccount(
            @RequestBody Map<String, String> request) {

        String identifier =
                request.getOrDefault("identifier", "").trim();

        return Map.of(
                "exists",
                !identifier.isBlank()
                        && userService.accountExists(identifier)
        );
    }

    // Forgot password - generate OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody com.project.user.dto.ForgotPasswordRequest request) {

        try {

            String identifier =
                    request.getIdentifier() == null
                            ? ""
                            : request.getIdentifier().trim();

            if (identifier.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Identifier is required");
            }

            String message =
                    userService.requestPasswordReset(identifier);

            return ResponseEntity.ok(
                    Map.of("message", message)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody com.project.user.dto.VerifyOtpRequest request) {

        String identifier =
                request.getIdentifier() == null
                        ? ""
                        : request.getIdentifier().trim();

        String otp =
                request.getOtp() == null
                        ? ""
                        : request.getOtp().trim();

        if (identifier.isBlank() || otp.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Identifier and OTP are required");
        }

        String resetToken =
                userService.verifyOtp(identifier, otp);

        if (resetToken == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP");
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "OTP verified successfully",
                        "resetToken", resetToken
                )
        );
    }

    // Reset password using OTP reset token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody com.project.user.dto.ResetPasswordRequest request) {

        String identifier =
                request.getIdentifier() == null
                        ? ""
                        : request.getIdentifier().trim();

        String resetToken =
                request.getResetToken() == null
                        ? ""
                        : request.getResetToken().trim();

        String newPassword =
                request.getNewPassword() == null
                        ? ""
                        : request.getNewPassword();

        if (identifier.isBlank()
                || resetToken.isBlank()
                || newPassword.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Identifier, reset token and new password are required");
        }

        boolean success =
                userService.resetPassword(
                        identifier,
                        resetToken,
                        newPassword
                );

        if (!success) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired reset token");
        }

        return ResponseEntity.ok(
                Map.of("message", "Password reset successfully")
        );
    }

    // Change password for authenticated user
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody com.project.user.dto.ChangePasswordRequest request,
            Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        String currentPassword =
                request.getCurrentPassword() == null
                        ? ""
                        : request.getCurrentPassword();

        String newPassword =
                request.getNewPassword() == null
                        ? ""
                        : request.getNewPassword();

        if (currentPassword.isBlank()
                || newPassword.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Current password and new password are required");
        }

        boolean success =
                userService.changePassword(
                        authentication.getName(),
                        currentPassword,
                        newPassword
                );

        if (!success) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Current password is incorrect");
        }

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }
}