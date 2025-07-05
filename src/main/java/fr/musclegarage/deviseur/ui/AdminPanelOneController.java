package fr.musclegarage.deviseur.ui;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.util.Database;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

public class AdminPanelOneController {
    @FXML
    private TableView<Category> table;
    @FXML
    private TableColumn<Category, String> colName, colImage;
    @FXML
    private TableColumn<Category, Void> colAction;
    @FXML
    private Button btnSave;

    private final ObservableList<Category> data = FXCollections.observableArrayList();
    private final List<Category> deleted = new ArrayList<>();
    private CategoryDao dao;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            dao = new CategoryDaoJdbc(conn);
            setupTable();
            loadData();
        } catch (Exception e) {
            // À remplacer par un logger si besoin
            e.printStackTrace();
        }
    }

    private void setupTable() {
        table.setEditable(true);

        // Colonne Nom de catégorie
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> {
            e.getRowValue().setName(e.getNewValue());
            markDirty();
        });

        // Colonne Image : clic pour importer
        colImage.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getImageFilename()));
        colImage.setCellFactory(tc -> new TableCell<Category, String>() {
            private final Label lbl = new Label();
            {
                lbl.getStyleClass().add("label-link");
                lbl.setOnMouseClicked(evt -> {
                    Category cat = getTableRow().getItem();
                    if (cat == null)
                        return;
                    FileChooser chooser = new FileChooser();
                    chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
                    File f = chooser.showOpenDialog(table.getScene().getWindow());
                    if (f != null) {
                        cat.setImageFilename(f.getName());
                        lbl.setText(f.getName());
                        markDirty();
                    }
                });
            }

            @Override
            protected void updateItem(String imgName, boolean empty) {
                super.updateItem(imgName, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    lbl.setText(imgName == null ? "(cliquer pour choisir)" : imgName);
                    setGraphic(lbl);
                }
            }
        });

        // Colonne Supprimer
        colAction.setCellFactory(param -> new TableCell<Category, Void>() {
            private final Button del = new Button("🗑️");
            {
                del.getStyleClass().add("button-icon");
                del.setOnAction(e -> {
                    Category c = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(c);
                    deleted.add(c);
                    markDirty();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        table.setItems(data);
    }

    private void loadData() throws Exception {
        data.clear();
        data.addAll(dao.findAll());
        deleted.clear();
        btnSave.setDisable(true);
    }

    @FXML
    private void onAdd() {
        Category c = new Category();
        c.setName("");
        c.setImageFilename("");
        data.add(c);
        table.getSelectionModel().select(c);
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            // Suppression des éléments marqués
            for (Category c : deleted) {
                if (c.getId() != 0)
                    dao.delete(c.getId());
            }
            // Insertion / mise à jour
            for (Category c : data) {
                if (c.getId() == 0)
                    dao.insert(c);
                else
                    dao.update(c);
            }
            loadData();
        } catch (Exception e) {
            // À remplacer par un logger si besoin
            e.printStackTrace();
        }
    }

    private void markDirty() {
        btnSave.setDisable(false);
    }
}
