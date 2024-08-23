package me.jazzy.e_commerce_app.service;

import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.dto.RegistrationBody;
import me.jazzy.e_commerce_app.exception.UserExistsException;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public void registerUser(RegistrationBody registrationBody) throws UserExistsException {
        if (userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserExistsException();
        }

        User user = new User();
        user.setUsername(registrationBody.getEmail());
        user.setEmail(registrationBody.getEmail());
        user.setPassword(registrationBody.getPassword());
        //todo: encrypt password!!
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        userRepository.save(user);
    }
}