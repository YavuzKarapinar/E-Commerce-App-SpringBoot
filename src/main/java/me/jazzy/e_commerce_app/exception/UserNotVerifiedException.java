package me.jazzy.e_commerce_app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNotVerifiedException extends Exception {
    private boolean newEmailSent;
}