package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import javafx.fxml.FXML;

public class AdminPanelOneController {
    @FXML
    public void onLogout() {
        try {
            App.showRegistration(); // ou App.showLogin() si vous préférez
        } catch (Exception e) {
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Impossible de se déconnecter : " + e.getMessage()).showAndWait();
        }
    }
}
