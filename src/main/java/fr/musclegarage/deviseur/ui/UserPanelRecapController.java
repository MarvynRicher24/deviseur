package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.DevisDao;
import fr.musclegarage.deviseur.dao.DevisDaoJdbc;
import fr.musclegarage.deviseur.dao.OptionDao;
import fr.musclegarage.deviseur.dao.OptionDaoJdbc;
import fr.musclegarage.deviseur.model.Devis;
import fr.musclegarage.deviseur.model.Option;
import fr.musclegarage.deviseur.model.OptionChoice;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UserPanelRecapController {
    @FXML
    private StackPane modelPane;
    @FXML
    private VBox detailsContainer;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();

            // 1) Image modèle de base
            String baseImg = QuoteSession.getModel().getModelImage();
            ImageView ivBase = new ImageView(new Image(
                    new File("uploads/models/" + baseImg).toURI().toString(), true));
            ivBase.setFitWidth(400);
            ivBase.setFitHeight(300);
            ivBase.setPreserveRatio(true);
            modelPane.getChildren().add(ivBase);

            // 2) Superposition des overlays d’options
            Map<Integer, OptionChoice> choices = QuoteSession.getAllChoices();
            for (OptionChoice oc : choices.values()) {
                File f = new File("uploads/options/" + oc.getOptionChoiceImage());
                if (f.exists()) {
                    ImageView iv = new ImageView(new Image(f.toURI().toString(), true));
                    iv.fitWidthProperty().bind(modelPane.widthProperty());
                    iv.fitHeightProperty().bind(modelPane.heightProperty());
                    iv.setPreserveRatio(true);
                    modelPane.getChildren().add(iv);
                }
            }

            // 3) Préparer map optionId→nom d’option
            OptionDao optDao = new OptionDaoJdbc(conn);
            Map<Integer, String> optionNames = optDao.findAll().stream()
                    .collect(Collectors.toMap(Option::getId, Option::getOptionName));

            // 4) Construire la liste des détails
            detailsContainer.getChildren().clear();
            detailsContainer.getChildren().add(new Label(
                    "Client : " + QuoteSession.getClient().getClientName()
                            + " " + QuoteSession.getClient().getClientSurname()));
            detailsContainer.getChildren().add(new Label(
                    "Catégorie : " + QuoteSession.getCategory().getCategoryName()));
            detailsContainer.getChildren().add(new Label(
                    "Modèle : " + QuoteSession.getModel().getModelName()
                            + " – " + QuoteSession.getModel().getModelPrice() + "€"));
            detailsContainer.getChildren().add(new Label(
                    "Moteur : " + QuoteSession.getMotor().getMotorName()
                            + " – " + QuoteSession.getMotor().getMotorPrice() + "€"));

            // Options + choix
            for (OptionChoice oc : choices.values()) {
                String optName = optionNames.get(oc.getOptionId());
                detailsContainer.getChildren().add(new Label("Option : " + optName));
                detailsContainer.getChildren().add(new Label(
                        "  Choix : " + oc.getOptionChoiceName()
                                + " – " + oc.getOptionChoicePrice() + "€"));
            }

            // 5) TOTAL en bas
            Label total = new Label("TOTAL : " + QuoteSession.getTotalPrice() + "€");
            total.getStyleClass().add("label-title");
            detailsContainer.getChildren().add(total);

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onPrev() {
        App.userBaseController.goToOption();
    }

    @FXML
    private void onSave() {
        try {
            Connection conn = Database.getConnection();
            DevisDao dao = new DevisDaoJdbc(conn);
            Devis d = new Devis();
            d.setUserId(QuoteSession.getUser().getId());
            d.setClientId(QuoteSession.getClient().getId());
            d.setDateCreated(LocalDateTime.now());
            d.setTotalPrice(QuoteSession.getTotalPrice());
            dao.insert(d);

            new Alert(Alert.AlertType.INFORMATION,
                    "Devis enregistré (ID : " + d.getId() + ").").showAndWait();
            App.userBaseController.onGoMenu();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage()).showAndWait();
        }
    }
}
