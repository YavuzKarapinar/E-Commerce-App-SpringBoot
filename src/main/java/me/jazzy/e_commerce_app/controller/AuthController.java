package me.jazzy.e_commerce_app.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.LoginBody;
import me.jazzy.e_commerce_app.dto.LoginResponse;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.EmailFailureException;
import me.jazzy.e_commerce_app.exception.UserExistsException;
import me.jazzy.e_commerce_app.exception.UserNotVerifiedException;
import me.jazzy.e_commerce_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    private ResponseEntity<LoginResponse> loginUser(@RequestBody LoginBody loginBody) {
        try {
            String jwt = userService.loginUser(loginBody);
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setJwt(jwt);
            loginResponse.setSuccess(true);
            return ResponseEntity.ok(loginResponse);
        } catch (UserNotVerifiedException e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (e.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            loginResponse.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponse);
        } catch (EmailFailureException e) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(false);
            String reason = "EMAIL_SENT_FAILURE";
            loginResponse.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        if (userService.verifyUser(token)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}