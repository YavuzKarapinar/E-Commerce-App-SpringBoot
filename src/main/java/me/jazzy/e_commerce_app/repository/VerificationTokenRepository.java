package me.jazzy.e_commerce_app.repository;

import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(User user);
}