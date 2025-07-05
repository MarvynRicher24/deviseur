package fr.musclegarage.deviseur.model;

import java.time.LocalDateTime;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String email;
    private final LocalDateTime createdAt;

    // ──> Construit “vide” pour insertion
    public User(String username, String passwordHash, String email) {
        this(0, username, passwordHash, email, null);
    }

    // Construit complet si besoin de lire depuis la BD
    public User(int id, String username, String passwordHash, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}