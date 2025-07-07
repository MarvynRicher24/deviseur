package fr.musclegarage.deviseur.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    private List<Label> steps;
    private int currentStep = 0;

    @FXML
    public void initialize() {
        // Ordre d’apparition des onglets
        steps = Arrays.asList(navClient, navCategory, navModel, navMotor, navOption, navRecap);
        // on désactive tous sauf le premier
        for (int i = 1; i < steps.size(); i++) {
            steps.get(i).setDisable(true);
        }
        // on affiche la 1re vue
        showStep(0);
    }

    // Débloque les accès aux autres pages
    public void goToCategory() {
        steps.get(1).setDisable(false);
        showStep(1);
    }

    /** Débloque et affiche l’étape Modèle (index 2) */
    public void goToModel() {
        steps.get(2).setDisable(false);
        showStep(2);
    }

    /** Débloque et affiche l’étape Moteur (index 3) */
    public void goToMotor() {
        steps.get(3).setDisable(false);
        showStep(3);
    }

        /** Débloque et affiche l’étape option (index 4) */
    public void goToOption() {
        steps.get(4).setDisable(false);
        showStep(4);
    }

            /** Débloque et affiche l’étape option (index 5) */
    public void goToRecap() {
        steps.get(5).setDisable(false);
        showStep(5);
    }

    /** Affiche l’étape `index` (0 = Client, 1 = Catégorie, …) */
    public void showStep(int index) {
        // Style actif
        for (Label nav : steps) {
            nav.getStyleClass().remove("nav-item-active");
        }
        Label activeNav = steps.get(index);
        activeNav.getStyleClass().add("nav-item-active");

        // Chargement FXML
        String fxmlPath;
        String id = activeNav.getId();
        if ("navClient".equals(id)) {
            fxmlPath = "/userPanelClient.fxml";
        } else if ("navCategory".equals(id)) {
            fxmlPath = "/userPanelCategory.fxml";
        } else if ("navModel".equals(id)) {
            fxmlPath = "/userPanelModel.fxml";
        } else if ("navMotor".equals(id)) {
            fxmlPath = "/userPanelMotor.fxml";
        } else if ("navOption".equals(id)) {
            fxmlPath = "/userPanelOption.fxml";
        } else if ("navRecap".equals(id)) {
            fxmlPath = "/userPanelRecap.fxml";
        } else {
            fxmlPath = null;
        }

        if (fxmlPath != null) {
            try {
                // on crée un loader pour attraper l’exception exacte
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Node view = loader.load();
                contentPane.getChildren().setAll(view);
            } catch (IOException ex) {
                // ici on affiche l’erreur ‘ex’ réel, pas le path de classe
                new Alert(Alert.AlertType.ERROR,
                        "Impossible de charger la vue \"" + fxmlPath + "\" :\n" + ex.getMessage()).showAndWait();
                ex.printStackTrace();
            }
        }
    }

    // Ces handlers sont liés dans le FXML, mais n’activent la navigation que si
    // l’onglet est débloqué
    @FXML
    public void onNavClient() {
        if (!navClient.isDisable())
            showStep(0);
    }

    @FXML
    public void onNavCategory() {
        if (!navCategory.isDisable())
            showStep(1);
    }

    @FXML
    public void onNavModel() {
        if (!navModel.isDisable())
            showStep(2);
    }

    @FXML
    public void onNavMotor() {
        if (!navMotor.isDisable())
            showStep(3);
    }

    @FXML
    public void onNavOption() {
        if (!navOption.isDisable())
            showStep(4);
    }

    @FXML
    public void onNavRecap() {
        if (!navRecap.isDisable())
            showStep(5);
    }

    /** Retour au menu principal, sans sauvegarde de devis */
    @FXML
    public void onGoMenu() {
        try {
            App.showMenu();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de revenir au menu : " + e.getMessage())
                    .showAndWait();
        }
    }
}
