package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class UserPanelBaseController {
    @FXML
    private StackPane contentPane;
    @FXML
    private Label navClient, navCategory, navModel, navMotor, navOption, navRecap;

    @FXML
    public void initialize() {
        onNavClient(); // charger immédiatement le formulaire client
    }

    private void switchTo(String fxmlPath, Label activeNav) {
        // Reset styles
        for (Label nav : new Label[] { navClient, navCategory, navModel, navMotor, navOption, navRecap }) {
            nav.getStyleClass().remove("nav-item-active");
        }
        activeNav.getStyleClass().add("nav-item-active");

        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible de charger la vue : " + ex.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    void onNavClient() {
        switchTo("/userPanelClient.fxml", navClient);
    }

    @FXML
    void onNavCategory() {
        switchTo("/userPanelCategory.fxml", navCategory);
    }

    @FXML
    void onNavModel() {
        switchTo("/userPanelModel.fxml", navModel);
    }

    @FXML
    void onNavMotor() {
        switchTo("/userPanelMotor.fxml", navMotor);
    }

    @FXML
    void onNavOption() {
        switchTo("/userPanelOption.fxml", navOption);
    }

    @FXML
    void onNavRecap() {
        switchTo("/userPanelRecap.fxml", navRecap);
    }

    @FXML
    void onLogout() {
        try {
            App.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
