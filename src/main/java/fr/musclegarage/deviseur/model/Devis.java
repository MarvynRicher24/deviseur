package fr.musclegarage.deviseur.model;

import java.time.LocalDateTime;

public class Devis {
    private int id;
    private int userId;
    private int clientId;
    private Integer modelId; // ← nouveau
    private Integer motorId; // ← nouveau
    private LocalDateTime dateCreated;
    private Integer totalPrice;

    public Devis() {
    }

    public Devis(int id, int userId, int clientId,
            Integer modelId, Integer motorId,
            LocalDateTime dateCreated, Integer totalPrice) {
        this.id = id;
        this.userId = userId;
        this.clientId = clientId;
        this.modelId = modelId;
        this.motorId = motorId;
        this.dateCreated = dateCreated;
        this.totalPrice = totalPrice;
    }

    // Getters & Setters
    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getMotorId() {
        return motorId;
    }

    public void setMotorId(Integer motorId) {
        this.motorId = motorId;
    }

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