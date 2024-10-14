package me.jazzy.e_commerce_app.controller;

import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.exception.UserHasNoPermissionException;
import me.jazzy.e_commerce_app.model.Address;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/address/{userId}")
    public ResponseEntity<List<Address>> getAddresses(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId
    ) {
        try {
            return ResponseEntity.ok(userService.getAddress(user, userId));
        } catch (UserHasNoPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/address/{userId}")
    public ResponseEntity<Address> putAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @RequestBody Address address
    ) {
        try {
            return ResponseEntity.ok(userService.putAddress(user, userId, address));
        } catch (UserHasNoPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/address/{userId}")
    public ResponseEntity<Address> postAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            @RequestBody Address address
    ) {
        try {
            return ResponseEntity.ok(userService.postAddress(user, userId, address));
        } catch (UserHasNoPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}