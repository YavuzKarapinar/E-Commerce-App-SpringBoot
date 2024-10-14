package me.jazzy.e_commerce_app.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.LoginBody;
import me.jazzy.e_commerce_app.dto.PasswordResetBody;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.*;
import me.jazzy.e_commerce_app.model.Address;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.model.VerificationToken;
import me.jazzy.e_commerce_app.repository.AddressRepository;
import me.jazzy.e_commerce_app.repository.UserRepository;
import me.jazzy.e_commerce_app.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private AddressRepository addressRepository;
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

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<User> op = userRepository.findByEmailIgnoreCase(email);
        if (op.isEmpty()) {
            throw new EmailNotFoundException();
        }

        User user = op.get();
        String token = jwtService.generatePasswordResetJWT(user);
        emailService.sendPasswordResetEmail(user, token);
    }

    public void resetPassword(PasswordResetBody passwordResetBody) throws EmailNotFoundException {
        String email = jwtService.getResetPasswordEmail(passwordResetBody.getToken());
        Optional<User> op = userRepository.findByEmailIgnoreCase(email);

        if (op.isEmpty()) {
            throw new EmailNotFoundException();
        }

        User user = op.get();
        user.setPassword(encryptionService.encryptPassword(passwordResetBody.getPassword()));
        userRepository.save(user);
    }

    public List<Address> getAddress(User user, Long userId) throws UserHasNoPermissionException {
        if (hasPermission(user, userId)) {
            return addressRepository.findByUser_Id(userId);
        }

        throw new UserHasNoPermissionException();
    }

    public Address putAddress(User user, Long userId, Address address) throws UserHasNoPermissionException {
        if (hasPermission(user, userId)) {
            User refUser = new User();
            refUser.setId(userId);
            address.setUser(refUser);
            return addressRepository.save(address);
        }

        throw new UserHasNoPermissionException();
    }

    public Address postAddress(User user, Long userId, Address address) throws UserHasNoPermissionException {
        if (hasPermission(user, userId)) {
            address.setId(null);
            User refUser = new User();
            refUser.setId(userId);
            address.setUser(refUser);
            return addressRepository.save(address);
        }

        throw new UserHasNoPermissionException();
    }

    private boolean hasPermission(User user, Long userId) {
        return user.getId().equals(userId);
    }
}