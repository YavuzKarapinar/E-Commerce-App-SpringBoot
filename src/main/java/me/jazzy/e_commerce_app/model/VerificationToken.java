package me.jazzy.e_commerce_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Timestamp createdTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}