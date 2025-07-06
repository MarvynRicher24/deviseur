package fr.musclegarage.deviseur.model;

public class Motor {
    private int id;
    private int categoryId;
    private String motorName;
    private int motorPrice;
    private int chMotor;
    private int torqueMotor;
    private String motorImage;

    public Motor() {
    }

    public Motor(int id, int categoryId, String motorName,
            int motorPrice, int chMotor, int torqueMotor,
            String motorImage) {
        this.id = id;
        this.categoryId = categoryId;
        this.motorName = motorName;
        this.motorPrice = motorPrice;
        this.chMotor = chMotor;
        this.torqueMotor = torqueMotor;
        this.motorImage = motorImage;
    }

    // getters & setters ...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getMotorName() {
        return motorName;
    }

    public void setMotorName(String motorName) {
        this.motorName = motorName;
    }

    public int getMotorPrice() {
        return motorPrice;
    }

    public void setMotorPrice(int motorPrice) {
        this.motorPrice = motorPrice;
    }

    public int getChMotor() {
        return chMotor;
    }

    public void setChMotor(int chMotor) {
        this.chMotor = chMotor;
    }

    public int getTorqueMotor() {
        return torqueMotor;
    }

    public void setTorqueMotor(int torqueMotor) {
        this.torqueMotor = torqueMotor;
    }

    public String getMotorImage() {
        return motorImage;
    }

    public void setMotorImage(String motorImage) {
        this.motorImage = motorImage;
    }
}