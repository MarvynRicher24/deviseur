package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.dao.MotorDao;
import fr.musclegarage.deviseur.dao.MotorDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.model.Motor;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class AdminPanelMotorController {
    @FXML
    private VBox listContainer;
    @FXML
    private Button btnSave;

    private MotorDao motorDao;
    private List<Category> categories;
    private final List<Motor> items = new ArrayList<>();
    private final List<Motor> deleted = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            CategoryDao catDao = new CategoryDaoJdbc(conn);
            categories = catDao.findAll();
            motorDao = new MotorDaoJdbc(conn);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        listContainer.getChildren().clear();
        items.clear();
        items.addAll(motorDao.findAll());
        deleted.clear();
        btnSave.setDisable(true);
        for (Motor m : items) {
            listContainer.getChildren().add(createRow(m));
        }
    }

    private VBox createRow(Motor m) {
        VBox container = new VBox(4);
        container.setStyle("-fx-background-color: white; -fx-padding: 8;");

        // Ligne 1: Catégorie & Moteur
        HBox line1 = new HBox(10);
        Label lblCat = new Label("Catégorie :");
        lblCat.setPrefWidth(80);
        ComboBox<Category> cbCat = new ComboBox<>();
        cbCat.getItems().addAll(categories);
        cbCat.setValue(categories.stream().filter(c -> c.getId() == m.getCategoryId()).findFirst().orElse(null));
        cbCat.setPrefWidth(150);
        cbCat.setOnAction(evt -> {
            m.setCategoryId(cbCat.getValue().getId());
            markDirty();
        });

        Label lblName = new Label("Moteur :");
        lblName.setPrefWidth(80);
        TextField tfName = new TextField(m.getMotorName());
        tfName.setPrefColumnCount(30);
        tfName.textProperty().addListener((o, old, n) -> {
            m.setMotorName(n);
            markDirty();
        });
        line1.getChildren().addAll(lblCat, cbCat, lblName, tfName);

        // Ligne 2: CH, Nm, Prix, Image, Supprimer
        HBox line2 = new HBox(10);
        Label lblCh = new Label("ch :");
        lblCh.setPrefWidth(40);
        TextField tfCh = new TextField(String.valueOf(m.getChMotor()));
        tfCh.setPrefColumnCount(4);
        tfCh.textProperty().addListener((o, old, n) -> {
            try {
                m.setChMotor(Integer.parseInt(n));
                markDirty();
            } catch (Exception ignored) {
            }
        });

        Label lblNm = new Label("Nm de couple :");
        lblNm.setPrefWidth(100);
        TextField tfNm = new TextField(String.valueOf(m.getTorqueMotor()));
        tfNm.setPrefColumnCount(4);
        tfNm.textProperty().addListener((o, old, n) -> {
            try {
                m.setTorqueMotor(Integer.parseInt(n));
                markDirty();
            } catch (Exception ignored) {
            }
        });

        Label lblPrice = new Label("Prix :");
        lblPrice.setPrefWidth(50);
        TextField tfPrice = new TextField(String.valueOf(m.getMotorPrice()));
        tfPrice.setPrefColumnCount(6);
        tfPrice.textProperty().addListener((o, old, n) -> {
            try {
                m.setMotorPrice(Integer.parseInt(n));
                markDirty();
            } catch (Exception ignored) {
            }
        });

        Label lblImg = new Label("Image :");
        lblImg.setPrefWidth(60);
        Button btnImport = new Button(
                (m.getMotorImage() == null || m.getMotorImage().isEmpty()) ? "Importer" : m.getMotorImage());
        btnImport.setPrefWidth(120);
        btnImport.setOnAction(evt -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File f = chooser.showOpenDialog(listContainer.getScene().getWindow());
            if (f != null) {
                try {
                    // 1) copie dans uploads/motors
                    Path targetDir = Paths.get("uploads", "motors");
                    Files.createDirectories(targetDir);
                    Path target = targetDir.resolve(f.getName());
                    Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

                    // 2) stocke juste le nom en mémoire (et via DAO)
                    m.setMotorImage(f.getName());
                    btnImport.setText(f.getName());
                    markDirty();

                } catch (IOException io) {
                    new Alert(Alert.AlertType.ERROR,
                            "Erreur copie image : " + io.getMessage()).showAndWait();
                }
            }
        });

        Button btnDel = new Button("Supprimer");
        btnDel.setOnAction(e -> {
            listContainer.getChildren().remove(container);
            deleted.add(m);
            markDirty();
        });

        line2.getChildren().addAll(lblCh, tfCh, lblNm, tfNm, lblPrice, tfPrice, lblImg, btnImport, btnDel);

        container.getChildren().addAll(line1, line2);
        return container;
    }

    @FXML
    private void onAdd() {
        Motor m = new Motor();
        m.setCategoryId(categories.get(0).getId());
        m.setMotorName("");
        m.setChMotor(0);
        m.setTorqueMotor(0);
        m.setMotorPrice(0);
        m.setMotorImage("");
        items.add(m);
        listContainer.getChildren().add(createRow(m));
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            for (Motor m : deleted)
                if (m.getId() != 0)
                    motorDao.delete(m.getId());
            for (Motor m : items) {
                if (m.getId() == 0)
                    motorDao.insert(m);
                else
                    motorDao.update(m);
            }
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markDirty() {
        btnSave.setDisable(false);
    }
}