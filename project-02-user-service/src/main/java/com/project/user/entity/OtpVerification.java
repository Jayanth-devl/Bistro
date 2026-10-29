package com.project.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    private String otp;

    private LocalDateTime expiresAt;

    private boolean verified;

    private String passwordResetToken;

    private LocalDateTime passwordResetTokenExpiresAt;

    public OtpVerification() {
    }

    public OtpVerification(
            String identifier,
            String otp,
            LocalDateTime expiresAt,
            boolean verified) {

        this.identifier = identifier;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.verified = verified;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiresAt() {
        return passwordResetTokenExpiresAt;
    }

    public void setPasswordResetTokenExpiresAt(
            LocalDateTime passwordResetTokenExpiresAt) {

        this.passwordResetTokenExpiresAt = passwordResetTokenExpiresAt;
    }
}