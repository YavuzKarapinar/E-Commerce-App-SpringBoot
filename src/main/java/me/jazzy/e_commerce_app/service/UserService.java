package me.jazzy.e_commerce_app.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.LoginBody;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.EmailFailureException;
import me.jazzy.e_commerce_app.exception.UserExistsException;
import me.jazzy.e_commerce_app.exception.UserNotVerifiedException;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.model.VerificationToken;
import me.jazzy.e_commerce_app.repository.UserRepository;
import me.jazzy.e_commerce_app.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenRepository verificationTokenRepository;

    private static boolean isTokenSentAtLeastOneHourAgo(List<VerificationToken> verificationTokens) {
        return verificationTokens.get(0).getCreatedTimestamp()
                .before(new Timestamp(System.currentTimeMillis() - (1000 * 60 * 60)));
    }

    public void registerUser(RegistrationBody registrationBody) throws UserExistsException, EmailFailureException {
        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserExistsException();
        }

        User user = new User();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);

        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);
    }

    private VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<User> op = userRepository.findByUsernameIgnoreCase(loginBody.getUsername());
        if (op.isEmpty()) {
            return null;
        }

        User user = op.get();
        if (!encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
            return null;
        }

        if (!user.getEmailVerified()) {
            List<VerificationToken> verificationTokens = user.getVerificationTokens();
            boolean resendToken = verificationTokens.isEmpty() || isTokenSentAtLeastOneHourAgo(verificationTokens);
            if (resendToken) {
                VerificationToken verificationToken = createVerificationToken(user);
                verificationTokenRepository.save(verificationToken);
                emailService.sendVerificationEmail(verificationToken);
            }

            throw new UserNotVerifiedException(resendToken);
        }

        return jwtService.generateJWT(user);
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> optional = verificationTokenRepository.findByToken(token);

        if (optional.isEmpty()) {
            return false;
        }

        VerificationToken verificationToken = optional.get();
        User user = verificationToken.getUser();
        if (user.getEmailVerified()) {
            return false;
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        verificationTokenRepository.deleteByUser(user);

        return true;
    }
}