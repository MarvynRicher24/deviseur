package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminPanelBaseController {
    @FXML
    private StackPane contentPane;
    @FXML
    private Label navCategory, navModel, navMotor, navOption, navOptionChoice;

    @FXML
    public void initialize() {
        onNavCategory(); // simule un clic sur le premier onglet
    }

    private void switchTo(String fxmlPath, Label activeNav) {
        // reset styles
        for (Label nav : new Label[] { navCategory, navModel, navMotor, navOption, navOptionChoice }) {
            nav.getStyleClass().remove("nav-item-active");
        }
        activeNav.getStyleClass().add("nav-item-active");

        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void onNavCategory() {
        switchTo("/adminPanelCategory.fxml", navCategory);
    }

    @FXML
    void onNavModel() {
        switchTo("/adminPanelModel.fxml", navModel);
    }

    @FXML
    void onNavMotor() {
        switchTo("/adminPanelMotor.fxml", navMotor);
    }

    @FXML
    void onNavOption() {
        switchTo("/adminPanelOption.fxml", navOption);
    }

    @FXML
    void onNavOptionChoice() {
        switchTo("/adminPanelOptionChoice.fxml", navOptionChoice);
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
