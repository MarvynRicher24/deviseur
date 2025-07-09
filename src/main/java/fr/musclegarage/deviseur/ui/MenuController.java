package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.model.QuoteSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MenuController {

    @FXML
    public void onCreateQuote() {
        try {
            // Réinitialiser tout le devis en session
            QuoteSession.reset();

            // Ouvrir l’interface de création
            App.showUserPanel();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible d’ouvrir le créateur de devis : " + e.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    public void onListQuotes() {
        try {
            App.showUserDevis();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible d’ouvrir Mes devis : " + e.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    public void onLogout() {
        try {
            App.showLogin();
        } catch (Exception e) {
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Impossible de revenir à l'inscription : " + e.getMessage()).showAndWait();
        }
    }
}
