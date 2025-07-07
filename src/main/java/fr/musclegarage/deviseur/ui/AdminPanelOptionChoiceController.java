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

import fr.musclegarage.deviseur.dao.ModelDao;
import fr.musclegarage.deviseur.dao.ModelDaoJdbc;
import fr.musclegarage.deviseur.dao.OptionChoiceDao;
import fr.musclegarage.deviseur.dao.OptionChoiceDaoJdbc;
import fr.musclegarage.deviseur.dao.OptionDao;
import fr.musclegarage.deviseur.dao.OptionDaoJdbc;
import fr.musclegarage.deviseur.model.Model;
import fr.musclegarage.deviseur.model.Option;
import fr.musclegarage.deviseur.model.OptionChoice;
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

public class AdminPanelOptionChoiceController {
    @FXML
    private VBox listContainer;
    @FXML
    private Button btnSave;

    private OptionChoiceDao ocDao;
    private List<Model> models;
    private List<Option> options;
    private final List<OptionChoice> items = new ArrayList<>();
    private final List<OptionChoice> deleted = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            ModelDao modelDao = new ModelDaoJdbc(conn);
            OptionDao optDao = new OptionDaoJdbc(conn);
            models = modelDao.findAll();
            options = optDao.findAll();
            ocDao = new OptionChoiceDaoJdbc(conn);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        listContainer.getChildren().clear();
        items.clear();
        deleted.clear();
        btnSave.setDisable(true);
        items.addAll(ocDao.findAll());
        for (OptionChoice oc : items)
            listContainer.getChildren().add(createRow(oc));
    }

    private VBox createRow(OptionChoice oc) {
        VBox container = new VBox(4);
        container.setStyle("-fx-background-color: white; -fx-padding: 8;");

        // Ligne 1: Modèle & Option
        HBox line1 = new HBox(10);
        Label lblMod = new Label("Modèle :");
        lblMod.setPrefWidth(80);
        ComboBox<Model> cbMod = new ComboBox<>();
        cbMod.getItems().addAll(models);
        cbMod.setValue(models.stream().filter(m -> m.getId() == oc.getModelId()).findFirst().orElse(null));
        cbMod.setPrefWidth(150);
        cbMod.setOnAction(e -> {
            oc.setModelId(cbMod.getValue().getId());
            markDirty();
        });

        Label lblOpt = new Label("Option :");
        lblOpt.setPrefWidth(80);
        ComboBox<Option> cbOpt = new ComboBox<>();
        cbOpt.getItems().addAll(options);
        cbOpt.setValue(options.stream().filter(o -> o.getId() == oc.getOptionId()).findFirst().orElse(null));
        cbOpt.setPrefWidth(150);
        cbOpt.setOnAction(e -> {
            oc.setOptionId(cbOpt.getValue().getId());
            markDirty();
        });

        line1.getChildren().addAll(lblMod, cbMod, lblOpt, cbOpt);

        // Ligne 2: Choix, Prix, Image, Supprimer
        HBox line2 = new HBox(10);
        Label lblChoice = new Label("Choix :");
        lblChoice.setPrefWidth(60);
        TextField tfChoice = new TextField(oc.getOptionChoiceName());
        tfChoice.setPrefColumnCount(30);
        tfChoice.textProperty().addListener((o, old, n) -> {
            oc.setOptionChoiceName(n);
            markDirty();
        });

        Label lblPrice = new Label("Prix :");
        lblPrice.setPrefWidth(50);
        TextField tfPrice = new TextField(String.valueOf(oc.getOptionChoicePrice()));
        tfPrice.setPrefColumnCount(6);
        tfPrice.textProperty().addListener((o, old, n) -> {
            try {
                oc.setOptionChoicePrice(Integer.parseInt(n));
                markDirty();
            } catch (Exception ignored) {
            }
        });

        Label lblImg = new Label("Image :");
        lblImg.setPrefWidth(60);
        Button btnImp = new Button(oc.getOptionChoiceImage() == null ? "Importer" : oc.getOptionChoiceImage());
        btnImp.setPrefWidth(120);
        btnImp.setOnAction(e -> {
            FileChooser ch = new FileChooser();
            ch.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File f = ch.showOpenDialog(listContainer.getScene().getWindow());
            if (f != null) {
                try {
                    // 1) Crée le dossier uploads/options s’il n’existe pas
                    Path targetDir = Paths.get("uploads", "options");
                    Files.createDirectories(targetDir);

                    // 2) Copie le fichier sélectionné dans ce dossier
                    Path target = targetDir.resolve(f.getName());
                    Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

                    // 3) Stocke uniquement le nom en mémoire (et lors de l’insert/update DB)
                    oc.setOptionChoiceImage(f.getName());
                    btnImp.setText(f.getName());
                    markDirty();

                } catch (IOException io) {
                    new Alert(Alert.AlertType.ERROR,
                            "Erreur lors de la copie de l’image : " + io.getMessage())
                            .showAndWait();
                }
            }
        });

        Button btnDel = new Button("Supprimer");
        btnDel.setOnAction(e -> {
            listContainer.getChildren().remove(container);
            deleted.add(oc);
            markDirty();
        });
        line2.getChildren().addAll(lblChoice, tfChoice, lblPrice, tfPrice, lblImg, btnImp, btnDel);

        container.getChildren().addAll(line1, line2);
        return container;
    }

    @FXML
    private void onAdd() {
        OptionChoice oc = new OptionChoice();
        oc.setModelId(models.get(0).getId());
        oc.setOptionId(options.get(0).getId());
        oc.setOptionChoiceName("");
        oc.setOptionChoicePrice(0);
        oc.setOptionChoiceImage("");
        items.add(oc);
        listContainer.getChildren().add(createRow(oc));
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            for (OptionChoice oc : deleted)
                if (oc.getId() != 0)
                    ocDao.delete(oc.getId());
            for (OptionChoice oc : items) {
                if (oc.getId() == 0)
                    ocDao.insert(oc);
                else
                    ocDao.update(oc);
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