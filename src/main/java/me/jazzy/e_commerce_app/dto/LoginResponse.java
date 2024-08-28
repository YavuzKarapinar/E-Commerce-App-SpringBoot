package me.jazzy.e_commerce_app.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String jwt;
    private boolean success;
    private String failureReason;
}