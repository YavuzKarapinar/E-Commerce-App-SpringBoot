package me.jazzy.e_commerce_app.service;

import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.LoginBody;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.UserExistsException;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;

    public void registerUser(RegistrationBody registrationBody) throws UserExistsException {
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

        userRepository.save(user);
    }

    public String loginUser(LoginBody loginBody) {
        Optional<User> op = userRepository.findByUsernameIgnoreCase(loginBody.getUsername());
        if (op.isEmpty()) {
            System.out.println("op empty");
            return null;
        }

        User user = op.get();
        if (!encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
            System.out.println("password verify");
            return null;
        }

        return jwtService.generateJWT(user);
    }
}