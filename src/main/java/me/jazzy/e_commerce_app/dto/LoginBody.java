package me.jazzy.e_commerce_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginBody {

    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String password;
}