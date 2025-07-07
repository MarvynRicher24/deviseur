package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserPanelCategoryController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox container;
    @FXML
    private Button btnNext;

    private List<Button> categoryButtons = new ArrayList<>();
    private List<Category> categories;
    private Category selected;
    private double scrollDelta;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            CategoryDao dao = new CategoryDaoJdbc(conn);
            categories = dao.findAll();
            scrollDelta = scrollPane.getPrefViewportWidth() / (container.getChildren().size() * (150 + 20));

            for (Category cat : categories) {
                // Image
                ImageView iv = new ImageView();
                iv.setFitWidth(150);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
                // suppose que les images sont dans resources/images/
                File imgFile = new File("uploads/categories/" + cat.getImageFilename());
                if (imgFile.exists()) {
                    iv.setImage(new Image(imgFile.toURI().toString(), true));
                } else {
                    // image par défaut si manquante
                    iv.setImage(new Image(getClass().getResource("/logo.png").toString()));
                }

                // Bouton
                Button btn = new Button(cat.getCategoryName());
                btn.setPrefWidth(150);
                btn.getStyleClass().add("button-primary");
                btn.setOnAction(e -> selectCategory(cat, btn));

                // Empilement image + bouton
                VBox box = new VBox(5, iv, btn);
                box.setStyle("-fx-alignment: center;");
                container.getChildren().add(box);
                categoryButtons.add(btn);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD: " + e.getMessage())
                    .showAndWait();
        }
        Category saved = QuoteSession.getCategory();
        if (saved != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == saved.getId()) {
                    // simule la sélection du bouton
                    selectCategory(saved, categoryButtons.get(i));
                    break;
                }
            }
        }
    }

    @FXML
    private void onScrollLeft() {
        double v = scrollPane.getHvalue() - scrollDelta;
        scrollPane.setHvalue(Math.max(v, 0));
    }

    @FXML
    private void onScrollRight() {
        double v = scrollPane.getHvalue() + scrollDelta;
        scrollPane.setHvalue(Math.min(v, 1));
    }

    private void selectCategory(Category cat, Button btn) {
        // réinitialise l’ancienne sélection
        if (selected != null) {
            for (Button b : categoryButtons) {
                b.getStyleClass().remove("button-selected");
            }
        }
        // marque la nouvelle
        selected = cat;
        btn.getStyleClass().add("button-selected");
        btnNext.setDisable(false);
    }

    @FXML
    private void onNext() {
        if (selected == null)
            return;
        // en session
        QuoteSession.setCategory(selected);
        // débloque et navigue
        App.userBaseController.goToModel();
    }

    @FXML
    private void onPrev() {
        // retour à la page précédente (Client)
        App.userBaseController.showStep(0);
    }
}
