package me.jazzy.e_commerce_app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationBody {

    @NotNull
    @NotBlank
    @Size(min = 3)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 8)
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    private String password;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;
}