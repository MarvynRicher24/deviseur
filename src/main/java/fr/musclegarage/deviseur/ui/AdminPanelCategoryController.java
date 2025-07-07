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
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class AdminPanelCategoryController {
    @FXML
    private VBox listContainer;
    @FXML
    private Button btnSave;

    private final List<Category> items = new ArrayList<>();
    private final List<Category> deleted = new ArrayList<>();
    private CategoryDao dao;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            dao = new CategoryDaoJdbc(conn);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        listContainer.getChildren().clear();
        items.clear();
        items.addAll(dao.findAll());
        for (Category c : items) {
            listContainer.getChildren().add(createRow(c));
        }
        deleted.clear();
        btnSave.setDisable(true);
    }

    private HBox createRow(Category c) {
        HBox row = new HBox(10);
        row.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-alignment: CENTER_LEFT;");

        // Label Nom et champ Nom
        Label lblName = new Label("Nom de catégorie :");
        lblName.setPrefWidth(120);
        TextField tfName = new TextField(c.getCategoryName());
        tfName.setPromptText("Entrez le nom");
        tfName.setPrefColumnCount(30);
        tfName.textProperty().addListener((obs, old, neu) -> {
            c.setCategoryName(neu);
            markDirty();
        });

        // Label Image et bouton Importer
        Label lblImg = new Label("Image :");
        lblImg.setPrefWidth(60);
        Button btnImport = new Button(c.getImageFilename().isEmpty() ? "Importer" : c.getImageFilename());
        btnImport.setPrefWidth(120);
        btnImport.setOnAction(evt -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File f = chooser.showOpenDialog(listContainer.getScene().getWindow());
            if (f != null) {
                try {
                    // 1) copie le fichier dans uploads/categories
                    Path targetDir = Paths.get("uploads", "categories");
                    Files.createDirectories(targetDir);
                    Path target = targetDir.resolve(f.getName());
                    Files.copy(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

                    // 2) stocke le nom seul en mémoire/DAO
                    c.setImageFilename(f.getName());
                    btnImport.setText(f.getName());

                    markDirty();
                } catch (IOException io) {
                    new Alert(Alert.AlertType.ERROR, "Erreur copie image : " + io.getMessage()).showAndWait();
                }
            }
        });

        // Bouton Supprimer
        Button btnDel = new Button("Supprimer");
        btnDel.setOnAction(e -> {
            listContainer.getChildren().remove(row);
            deleted.add(c);
            markDirty();
        });

        row.getChildren().addAll(lblName, tfName, lblImg, btnImport, btnDel);
        return row;
    }

    @FXML
    private void onAdd() {
        Category c = new Category();
        c.setCategoryName("");
        c.setImageFilename("");
        items.add(c);
        listContainer.getChildren().add(createRow(c));
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            // supprimer
            for (Category c : deleted) {
                if (c.getId() != 0)
                    dao.delete(c.getId());
            }
            // insert/update
            for (Category c : items) {
                if (c.getId() == 0)
                    dao.insert(c);
                else
                    dao.update(c);
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