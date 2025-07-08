package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MenuController {

    @FXML
    public void onCreateQuote() {
        try {
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
