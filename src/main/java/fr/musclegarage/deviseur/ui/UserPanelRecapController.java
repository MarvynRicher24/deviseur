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
import fr.musclegarage.deviseur.dao.DevisOptionChoiceLinkDao;
import fr.musclegarage.deviseur.dao.DevisOptionChoiceLinkDaoJdbc;
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
            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
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
            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
                String optName = oc.getOptionChoiceName();
                detailsContainer.getChildren().add(new Label(
                        "Option sélectionnée : " + optName
                                + " – " + oc.getOptionChoicePrice() + "€"));
            }

            // 5) TOTAL en bas
            Label total = new Label("TOTAL : " + QuoteSession.getTotalPrice() + "€");
            total.getStyleClass().add("label-title");
            detailsContainer.getChildren().add(total);

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
            DevisOptionChoiceLinkDao linkDao = new DevisOptionChoiceLinkDaoJdbc(conn);

            // 1) Création ou MAJ du devis
            Devis d = new Devis();
            d.setUserId(QuoteSession.getUser().getId());
            d.setClientId(QuoteSession.getClient().getId());
            d.setDateCreated(LocalDateTime.now());
            d.setTotalPrice(QuoteSession.getTotalPrice());

            // Si on est en édition (id != 0), on supprime les anciens liens
            if (d.getId() != 0) {
                linkDao.deleteByDevisId(d.getId());
            }

            dao.insert(d); // génère l'id si nouveau
            int devisId = d.getId();

            // 2) Insertion des choix en base
            for (OptionChoice oc : QuoteSession.getAllChoices().values()) {
                linkDao.insertLink(devisId, oc.getId());
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Devis enregistré (ID : " + devisId + ").").showAndWait();
            App.userBaseController.onGoMenu();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + e.getMessage()).showAndWait();
        }
    }
}
