package fr.musclegarage.deviseur.model;

public class QuoteSession {
    private static Client currentClient;
    private static Category currentCategory;
    private static Model currentModel;
    private static Motor currentMotor;

    public static void setClient(Client client) { currentClient = client; }
    public static Client getClient() { return currentClient; }

    public static void setCategory(Category category) { currentCategory = category; }
    public static Category getCategory() { return currentCategory; }

    public static void setModel(Model model) { currentModel = model; }
    public static Model getModel() { return currentModel; }

    public static void setMotor(Motor motor) { currentMotor = motor; }
    public static Motor getMotor() { return currentMotor; }

    /** Calcule le total courant (ici : prix du modèle, à étendre plus tard) */
    public static int getTotalPrice() {
        int total = 0;
        if (currentModel != null) total += currentModel.getModelPrice();
        // plus tard : ajouter options, moteurs, etc.
        return total;
    }
}
