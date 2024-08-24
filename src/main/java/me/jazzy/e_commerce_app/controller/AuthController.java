package me.jazzy.e_commerce_app.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.LoginBody;
import me.jazzy.e_commerce_app.dto.LoginResponse;
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

    @PostMapping("/login")
    private ResponseEntity<LoginResponse> loginUser(@RequestBody LoginBody loginBody) {
        String jwt = userService.loginUser(loginBody);
        System.out.println(jwt);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(jwt);
        return ResponseEntity.ok(loginResponse);
    }
}