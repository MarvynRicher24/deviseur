package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.OptionChoiceDao;
import fr.musclegarage.deviseur.dao.OptionChoiceDaoJdbc;
import fr.musclegarage.deviseur.dao.OptionDao;
import fr.musclegarage.deviseur.dao.OptionDaoJdbc;
import fr.musclegarage.deviseur.model.Option;
import fr.musclegarage.deviseur.model.OptionChoice;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UserPanelOptionController {
    @FXML
    private StackPane modelPane;
    @FXML
    private ImageView ivModelBase;
    @FXML
    private VBox optionsContainer;
    @FXML
    private Label lblTotal;

    // Overlay par option
    private final Map<Integer, ImageView> overlays = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        // 1) Image de base du modèle
        String baseImg = QuoteSession.getModel().getModelImage();
        File f = new File("uploads/models/" + baseImg);
        if (f.exists()) {
            ivModelBase.setImage(new Image(f.toURI().toString(), true));
        }

        try {
            Connection conn = Database.getConnection();
            OptionDao optDao = new OptionDaoJdbc(conn);
            OptionChoiceDao ocDao = new OptionChoiceDaoJdbc(conn);

            // Charge toutes les OptionChoice
            List<OptionChoice> allChoices = ocDao.findAll();

            // Sélection déjà faite en session
            Map<Integer, OptionChoice> savedChoices = QuoteSession.getAllChoices();

            // 2) Charge les options
            List<Option> options = optDao.findAll().stream()
                    .filter(o -> allChoices.stream()
                            .anyMatch(c -> c.getOptionId() == o.getId()
                                    && c.getModelId() == QuoteSession.getModel().getId()))
                    .collect(Collectors.toList());

            for (Option o : options) {
                Label lblOpt = new Label(o.getOptionName());
                lblOpt.getStyleClass().add("label-title");

                HBox hbox = new HBox(10);
                List<OptionChoice> choices = allChoices.stream()
                        .filter(c -> c.getOptionId() == o.getId()
                                && c.getModelId() == QuoteSession.getModel().getId())
                        .collect(Collectors.toList());

                for (OptionChoice c : choices) {
                    Button btn = new Button(c.getOptionChoiceName() + "\n" + c.getOptionChoicePrice() + "€");
                    btn.getStyleClass().add("button-primary");
                    btn.setOnAction(evt -> selectOptionChoice(o.getId(), c, btn));
                    hbox.getChildren().add(btn);

                    // Si l'utilisateur avait déjà choisi cette OptionChoice,
                    // on applique la sélection tout de suite :
                    OptionChoice saved = savedChoices.get(o.getId());
                    if (saved != null && saved.getId() == c.getId()) {
                        selectOptionChoice(o.getId(), c, btn);
                    }
                }

                optionsContainer.getChildren().addAll(lblOpt, hbox);
            }

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + ex.getMessage()).showAndWait();
        }

        // Total initial
        lblTotal.setText(QuoteSession.getTotalPrice() + "€");
    }

    private void selectOptionChoice(int optionId, OptionChoice choice, Button btn) {
        // 1) session
        QuoteSession.setChoice(optionId, choice);

        // 2) visuel boutons : désélectionne tous dans ce HBox
        optionsContainer.getChildren().stream()
                .filter(n -> n instanceof HBox)
                .map(n -> (HBox) n)
                .filter(h -> h.getChildren().contains(btn))
                .findFirst()
                .ifPresent(h -> {
                    h.getChildren().forEach(b -> b.getStyleClass().remove("button-selected"));
                });
        btn.getStyleClass().add("button-selected");

        // 3) gestion de l’overlay
        // retire l’ancien
        ImageView old = overlays.remove(optionId);
        if (old != null) {
            modelPane.getChildren().remove(old);
        }
        // ajoute le nouveau
        File img = new File("uploads/options/" + choice.getOptionChoiceImage());
        if (img.exists()) {
            ImageView iv = new ImageView(new Image(img.toURI().toString(), true));

            // IMPORTANT : lier sa taille à celle du modelPane
            iv.fitWidthProperty().bind(modelPane.widthProperty());
            iv.fitHeightProperty().bind(modelPane.heightProperty());
            iv.setPreserveRatio(true);

            // Centrage dans le StackPane
            StackPane.setAlignment(iv, Pos.CENTER);

            // Ajout en dernier (au‑dessus)
            modelPane.getChildren().add(iv);
            overlays.put(optionId, iv);
        }

        // 5) mise à jour du total
        lblTotal.setText(QuoteSession.getTotalPrice() + "€");
    }

    @FXML
    private void onPrev() {
        App.userBaseController.goToMotor();
    }

    @FXML
    private void onNext() {
        App.userBaseController.goToRecap();
    }
}
