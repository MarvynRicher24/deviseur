package fr.musclegarage.deviseur.model;

public class QuoteSession {
    private static Client currentClient;
    // futures étapes : private static Category category; etc.

    public static void setClient(Client client) {
        currentClient = client;
    }

    public static Client getClient() {
        return currentClient;
    }
}
