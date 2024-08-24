package me.jazzy.e_commerce_app.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import me.jazzy.e_commerce_app.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(User user) {
        return JWT
                .create()
                .withClaim("USERNAME", user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds))
                .withIssuer(issuer)
                .sign(algorithm);
    }
}