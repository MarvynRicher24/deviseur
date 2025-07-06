// src/main/java/fr/musclegarage/deviseur/model/Client.java
package fr.musclegarage.deviseur.model;

public class Client {
    private int id;
    private String clientName;
    private String clientSurname;
    private String clientAddress;

    public Client() {
    }

    public Client(int id, String clientName, String clientSurname, String clientAddress) {
        this.id = id;
        this.clientName = clientName;
        this.clientSurname = clientSurname;
        this.clientAddress = clientAddress;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSurname() {
        return clientSurname;
    }

    public void setClientSurname(String clientSurname) {
        this.clientSurname = clientSurname;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public String toString() {
        return clientName + " " + clientSurname;
    }
}