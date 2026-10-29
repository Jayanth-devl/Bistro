package com.project.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.user.entity.LoginAttempt;
import com.project.user.repository.LoginAttemptRepository;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int BLOCK_MINUTES = 10;

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(
            LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    public boolean isBlocked(String email) {

        LoginAttempt attempt =
                loginAttemptRepository.findByEmail(email)
                        .orElse(null);

        if (attempt == null) {
            return false;
        }

        if (attempt.getBlockedUntil() == null) {
            return false;
        }

        if (attempt.getBlockedUntil()
                .isAfter(LocalDateTime.now())) {
            return true;
        }

        attempt.setFailedAttempts(0);
        attempt.setBlockedUntil(null);
        loginAttemptRepository.save(attempt);

        return false;
    }

    public void recordFailedAttempt(String email) {

        LoginAttempt attempt =
                loginAttemptRepository.findByEmail(email)
                        .orElseGet(() ->
                                new LoginAttempt(
                                        email,
                                        0,
                                        null));

        int failedAttempts =
                attempt.getFailedAttempts() + 1;

        attempt.setFailedAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            attempt.setBlockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(BLOCK_MINUTES));
        }

        loginAttemptRepository.save(attempt);
    }

    public void resetAttempts(String email) {

        LoginAttempt attempt =
                loginAttemptRepository.findByEmail(email)
                        .orElse(null);

        if (attempt != null) {
            attempt.setFailedAttempts(0);
            attempt.setBlockedUntil(null);
            loginAttemptRepository.save(attempt);
        }
    }
}