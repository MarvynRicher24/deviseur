package fr.musclegarage.deviseur.model;

import java.util.HashMap;
import java.util.Map;

public class QuoteSession {
    private static User currentUser;
    private static Client currentClient;
    private static Category currentCategory;
    private static Model currentModel;
    private static Motor currentMotor;
    private static Map<Integer, OptionChoice> choices = new HashMap<>();
    private static Integer currentDevisId;

    // User
    public static void setUser(User u) {
        currentUser = u;
    }

    public static User getUser() {
        return currentUser;
    }

    // Client
    public static void setClient(Client client) {
        currentClient = client;
    }

    public static Client getClient() {
        return currentClient;
    }

    // Catégorie
    public static void setCategory(Category category) {
        currentCategory = category;
    }

    public static Category getCategory() {
        return currentCategory;
    }

    // Modèle
    public static void setModel(Model model) {
        currentModel = model;
    }

    public static Model getModel() {
        return currentModel;
    }

    // Moteur
    public static void setMotor(Motor motor) {
        currentMotor = motor;
    }

    public static Motor getMotor() {
        return currentMotor;
    }

    public static void setChoice(int optionId, OptionChoice choice) {
        choices.put(optionId, choice);
    }

    public static OptionChoice getChoice(int optionId) {
        return choices.get(optionId);
    }

    public static Map<Integer, OptionChoice> getAllChoices() {
        return choices;
    }

    public static void setDevisId(Integer id) {
        currentDevisId = id;
    }

    public static Integer getDevisId() {
        return currentDevisId;
    }

    /** Calcule le total courant (ici : prix du modèle, à étendre plus tard) */
    public static int getTotalPrice() {
        int total = 0;
        if (currentModel != null)
            total += currentModel.getModelPrice();
        if (currentMotor != null)
            total += currentMotor.getMotorPrice();
        for (OptionChoice oc : choices.values()) {
            total += oc.getOptionChoicePrice();
        }
        return total;
    }

    public static void reset() {
        currentClient = null;
        currentCategory = null;
        currentModel = null;
        currentMotor = null;
        choices.clear();
        currentDevisId = null;
        // on ne touche pas à currentUser (utilisateur reste connecté)
    }
}
