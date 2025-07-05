package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    public void onCreateQuote() {
        System.out.println("→ Créer devis");
    }

    @FXML
    public void onListQuotes() {
        System.out.println("→ Mes devis");
    }

    @FXML
    public void onLogout() {
        try {
            App.showLogin(); // ou App.showLogin() selon votre flux
        } catch (Exception e) {
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Impossible de revenir à l'inscription : " + e.getMessage()).showAndWait();
        }
    }
}
