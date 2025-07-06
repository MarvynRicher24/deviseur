package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.dao.ModelDao;
import fr.musclegarage.deviseur.dao.ModelDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.model.Model;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class AdminPanelModelController {
    @FXML
    private VBox listContainer;
    @FXML
    private Button btnSave;

    private final List<Model> items = new ArrayList<>();
    private final List<Model> deleted = new ArrayList<>();
    private ModelDao modelDao;
    private List<Category> categories;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection(); // **pas** de try-with-resources
            CategoryDao catDao = new CategoryDaoJdbc(conn);
            categories = catDao.findAll();

            modelDao = new ModelDaoJdbc(conn); // on garde la même connexion ouverte
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        listContainer.getChildren().clear();
        items.clear();
        items.addAll(modelDao.findAll());
        deleted.clear();
        btnSave.setDisable(true);
        for (Model m : items) {
            listContainer.getChildren().add(createRow(m));
        }
    }

    private VBox createRow(Model m) {
        // Container deux lignes
        VBox container = new VBox(4);
        container.setStyle("-fx-background-color: white; -fx-padding: 8;");

        // Ligne 1 : Catégorie & Nom
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

        Label lblName = new Label("Modèle véhicule :");
        lblName.setPrefWidth(120);
        TextField tfName = new TextField(m.getModelName());
        tfName.setPrefColumnCount(30);
        tfName.textProperty().addListener((obs, o, n) -> {
            m.setModelName(n);
            markDirty();
        });

        line1.getChildren().addAll(lblCat, cbCat, lblName, tfName);

        // Ligne 2 : Prix, Image, Supprimer
        HBox line2 = new HBox(10);
        Label lblPrice = new Label("Prix :");
        lblPrice.setPrefWidth(50);
        TextField tfPrice = new TextField(String.valueOf(m.getModelPrice()));
        tfPrice.setPrefColumnCount(6);
        tfPrice.textProperty().addListener((obs, o, n) -> {
            try {
                m.setModelPrice(Integer.parseInt(n));
                markDirty();
            } catch (NumberFormatException ex) {
            }
        });

        Label lblImg = new Label("Image :");
        lblImg.setPrefWidth(60);
        Button btnImport = new Button(
                (m.getModelImage() == null || m.getModelImage().isEmpty()) ? "Importer" : m.getModelImage());
        btnImport.setPrefWidth(120);
        btnImport.setOnAction(evt -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File f = chooser.showOpenDialog(listContainer.getScene().getWindow());
            if (f != null) {
                m.setModelImage(f.getName());
                btnImport.setText(f.getName());
                markDirty();
            }
        });

        Button btnDel = new Button("Supprimer");
        btnDel.setOnAction(e -> {
            listContainer.getChildren().remove(container);
            items.remove(m);
            deleted.add(m);
            markDirty();
        });

        line2.getChildren().addAll(lblPrice, tfPrice, lblImg, btnImport, btnDel);

        container.getChildren().addAll(line1, line2);
        return container;
    }

    @FXML
    private void onAdd() {
        Model m = new Model();
        m.setCategoryId(categories.get(0).getId());
        m.setModelName("");
        m.setModelPrice(0);
        m.setModelImage("");
        items.add(m);
        listContainer.getChildren().add(createRow(m));
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            for (Model m : deleted)
                if (m.getId() != 0)
                    modelDao.delete(m.getId());
            for (Model m : items) {
                if (m.getId() == 0)
                    modelDao.insert(m);
                else
                    modelDao.update(m);
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