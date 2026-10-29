package com.project.user.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.user.entity.OtpVerification;
import com.project.user.repository.OtpVerificationRepository;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;

    public OtpService(OtpVerificationRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String generateOtp() {

        String otp = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        return otp;
    }

    public OtpVerification createOtp(String identifier) {

        String otp = generateOtp();

        OtpVerification verification =
                new OtpVerification(
                        identifier,
                        otp,
                        LocalDateTime.now().plusMinutes(5),
                        false
                );

        return otpRepository.save(verification);
    }

    public String verifyOtp(String identifier, String otp) {

        OtpVerification verification =
                otpRepository
                        .findTopByIdentifierOrderByIdDesc(identifier)
                        .orElse(null);

        if (verification == null) {
            return null;
        }

        if (verification.isVerified()) {
            return null;
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            return null;
        }

        if (!verification.getOtp().equals(otp)) {
            return null;
        }

        verification.setVerified(true);

        String resetToken = UUID.randomUUID().toString();

        verification.setPasswordResetToken(resetToken);

        verification.setPasswordResetTokenExpiresAt(
                LocalDateTime.now().plusMinutes(10)
        );

        otpRepository.save(verification);

        return resetToken;
    }
 // Validate password reset token
    public OtpVerification validateResetToken(
            String identifier,
            String resetToken) {

        OtpVerification verification =
                otpRepository
                        .findTopByIdentifierOrderByIdDesc(identifier)
                        .orElse(null);

        if (verification == null) {
            return null;
        }

        if (!verification.isVerified()) {
            return null;
        }

        if (verification.getPasswordResetToken() == null) {
            return null;
        }

        if (!verification.getPasswordResetToken().equals(resetToken)) {
            return null;
        }

        if (verification.getPasswordResetTokenExpiresAt()
                .isBefore(LocalDateTime.now())) {
            return null;
        }

        return verification;
    }
}