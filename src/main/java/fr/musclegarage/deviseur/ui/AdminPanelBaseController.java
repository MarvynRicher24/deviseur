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
    private Label navCategory, navModel, navEngine, navOption, navSubOption;

    @FXML
    public void initialize() {
        onNavCategory(); // simule un clic sur le premier onglet
    }

    private void switchTo(String fxmlPath, Label activeNav) {
        // reset styles
        for (Label nav : new Label[] { navCategory, navModel, navEngine, navOption, navSubOption }) {
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
        switchTo("/adminPanelOne.fxml", navCategory);
    }

    @FXML
    void onNavModel() {
        switchTo("/adminPanelTwo.fxml", navModel);
    }

    @FXML
    void onNavEngine() {
        switchTo("/adminPanelThree.fxml", navEngine);
    }

    @FXML
    void onNavOption() {
        switchTo("/adminPanelFour.fxml", navOption);
    }

    @FXML
    void onNavSubOption() {
        switchTo("/adminPanelFive.fxml", navSubOption);
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
