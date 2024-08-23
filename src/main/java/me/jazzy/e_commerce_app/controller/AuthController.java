package me.jazzy.e_commerce_app.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.UserExistsException;
import me.jazzy.e_commerce_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<Void> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}