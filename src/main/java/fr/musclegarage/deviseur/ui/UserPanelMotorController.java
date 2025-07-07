package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.MotorDao;
import fr.musclegarage.deviseur.dao.MotorDaoJdbc;
import fr.musclegarage.deviseur.model.Motor;
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

public class UserPanelMotorController {
    @FXML
    private Label lblTotal;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox container;
    @FXML
    private Button btnNext;

    private final List<Button> motorButtons = new ArrayList<>();
    private List<Motor> motors;
    private Motor selected;
    private double scrollDelta;

    @FXML
    public void initialize() {
        // total initial
        lblTotal.setText(QuoteSession.getTotalPrice() + "€");

        try {
            Connection conn = Database.getConnection();
            MotorDao dao = new MotorDaoJdbc(conn);
            // filtre par catégorie
            motors = dao.findAll().stream()
                    .filter(m -> m.getCategoryId() == QuoteSession.getCategory().getId())
                    .collect(Collectors.toList());

            scrollDelta = scrollPane.getPrefViewportWidth() / (motors.size() * (150 + 20));

            for (Motor m : motors) {
                ImageView iv = new ImageView();
                iv.setFitWidth(150);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
                File imgFile = new File("uploads/motors/" + m.getMotorImage());
                if (imgFile.exists())
                    iv.setImage(new Image(imgFile.toURI().toString(), true));
                else
                    iv.setImage(new Image(getClass().getResource("/logo.png").toString()));

                Button btn = new Button(m.getMotorName() + "\n" + m.getMotorPrice() + "€");
                btn.setPrefWidth(150);
                btn.getStyleClass().add("button-primary");
                btn.setOnAction(e -> selectMotor(m, btn));

                VBox box = new VBox(5, iv, btn);
                box.setStyle("-fx-alignment: center;");
                container.getChildren().add(box);
                motorButtons.add(btn);
            }

            // réapplique sélection si déjà en session
            Motor saved = QuoteSession.getMotor();
            if (saved != null) {
                for (int i = 0; i < motors.size(); i++) {
                    if (motors.get(i).getId() == saved.getId()) {
                        selectMotor(saved, motorButtons.get(i));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD: " + e.getMessage()).showAndWait();
        }
    }

    private void selectMotor(Motor m, Button btn) {
        motorButtons.forEach(b -> b.getStyleClass().remove("button-selected"));
        selected = m;
        btn.getStyleClass().add("button-selected");
        btnNext.setDisable(false);

        QuoteSession.setMotor(m);
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
        App.userBaseController.goToModel();
    }

    @FXML
    private void onNext() {
        if (selected == null)
            return;
        App.userBaseController.goToOption(); // prévoir cette méthode
    }
}
