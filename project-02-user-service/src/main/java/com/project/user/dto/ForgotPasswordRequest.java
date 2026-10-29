package com.project.user.dto;

public class ForgotPasswordRequest {

    private String identifier;

    public ForgotPasswordRequest() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}