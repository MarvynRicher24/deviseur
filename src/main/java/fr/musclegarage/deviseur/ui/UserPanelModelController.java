package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.ModelDao;
import fr.musclegarage.deviseur.dao.ModelDaoJdbc;
import fr.musclegarage.deviseur.model.Model;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserPanelModelController {
    @FXML
    private Label lblTotal;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox container;
    @FXML
    private Button btnNext;

    private List<Button> modelButtons = new ArrayList<>();
    private List<Model> models;
    private Model selected;
    private double scrollDelta;

    @FXML
    public void initialize() {
        // met à jour le total initial
        lblTotal.setText(QuoteSession.getTotalPrice() + "€");

        try {
            Connection conn = Database.getConnection();
            ModelDao dao = new ModelDaoJdbc(conn);
            // charge uniquement les modèles de la catégorie choisie
            models = dao.findAll().stream()
                    .filter(m -> m.getCategoryId() == QuoteSession.getCategory().getId())
                    .collect(Collectors.toList());

            scrollDelta = scrollPane.getPrefViewportWidth() / (models.size() * (150 + 20));

            for (Model m : models) {
                // Image
                ImageView iv = new ImageView();
                iv.setFitWidth(150);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
                File imgFile = new File("uploads/models/" + m.getModelImage());
                if (imgFile.exists()) {
                    iv.setImage(new Image(imgFile.toURI().toString(), true));
                } else {
                    iv.setImage(new Image(getClass().getResource("/logo.png").toString()));
                }

                // Bouton
                Button btn = new Button(m.getModelName() + "\n" + m.getModelPrice() + "€");
                btn.setPrefWidth(150);
                btn.getStyleClass().add("button-primary");
                btn.setOnAction(e -> selectModel(m, btn));

                // Empilement
                VBox box = new VBox(5, iv, btn);
                box.setStyle("-fx-alignment: center;");
                container.getChildren().add(box);
                modelButtons.add(btn);
            }

            // si déjà en session, réappliquer la sélection
            Model saved = QuoteSession.getModel();
            if (saved != null) {
                for (int i = 0; i < models.size(); i++) {
                    if (models.get(i).getId() == saved.getId()) {
                        selectModel(saved, modelButtons.get(i));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD: " + e.getMessage()).showAndWait();
        }
    }

    private void selectModel(Model m, Button btn) {
        // désélection
        modelButtons.forEach(b -> b.getStyleClass().remove("button-selected"));
        // sélection
        selected = m;
        btn.getStyleClass().add("button-selected");
        btnNext.setDisable(false);

        // mettre à jour session et total
        QuoteSession.setModel(m);
        lblTotal.setText(QuoteSession.getTotalPrice() + "€");
    }

    @FXML
    private void onScrollLeft() {
        scrollPane.setHvalue(Math.max(scrollPane.getHvalue() - scrollDelta, 0));
    }

    @FXML
    private void onScrollRight() {
        scrollPane.setHvalue(Math.min(scrollPane.getHvalue() + scrollDelta, 1));
    }

    @FXML
    private void onPrev() {
        App.userBaseController.goToCategory();
    }

    @FXML
    private void onNext() {
        if (selected == null)
            return;
        // passe à la page suivante (Moteur)
        App.userBaseController.goToMotor();
    }
}
