package fr.musclegarage.deviseur.model;

import java.time.LocalDateTime;

public class Devis {
    private int id;
    private int userId;
    private int clientId;
    private LocalDateTime dateCreated;
    private Integer totalPrice;

    public Devis() {
    }

    public Devis(int id, int userId, int clientId, LocalDateTime dateCreated, Integer totalPrice) {
        this.id = id;
        this.userId = userId;
        this.clientId = clientId;
        this.dateCreated = dateCreated;
        this.totalPrice = totalPrice;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}