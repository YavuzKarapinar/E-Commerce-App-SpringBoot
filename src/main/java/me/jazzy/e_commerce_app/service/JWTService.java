package me.jazzy.e_commerce_app.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import me.jazzy.e_commerce_app.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    private final String USERNAME_KEY = "USERNAME";
    private final String EMAIL_KEY = "EMAIL";
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
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(User user) {
        return JWT
                .create()
                .withClaim(EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiryInSeconds))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        return jwt
                .getClaim(USERNAME_KEY)
                .asString();
    }
}