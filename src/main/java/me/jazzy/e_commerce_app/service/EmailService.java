package me.jazzy.e_commerce_app.service;

import lombok.RequiredArgsConstructor;
import me.jazzy.e_commerce_app.exception.EmailFailureException;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${email.from}")
    private String fromAddress;
    @Value("${app.frontend.url}")
    private String baseUrl;

    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to active your account.");
        message.setText("Please follow the link below to verify your email to active your account\n" +
                baseUrl + "/auth/verify?token=" + verificationToken.getToken());
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException();
        }
    }

    public void sendPasswordResetEmail(User user, String token) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText("You requested password reset on our website. Please" +
                "find the link below to be able to reset your password.\n" +
                baseUrl + "/auth/reset?token=" + token);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailFailureException();
        }
    }
}