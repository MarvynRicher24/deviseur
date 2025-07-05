package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.util.Callback;

import java.sql.Connection;

public class AdminPanelOneController {
    @FXML
    private TableView<Category> table;
    @FXML
    private TableColumn<Category, Number> colId;
    @FXML
    private TableColumn<Category, String> colName, colImage;
    @FXML
    private TableColumn<Category, Void> colAction;
    @FXML
    private Button btnSave;

    private final ObservableList<Category> data = FXCollections.observableArrayList();
    private CategoryDao dao;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            dao = new CategoryDaoJdbc(conn);
            setupTable();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        table.setEditable(true);
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colName.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
        colImage.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getImageFilename()));

        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> {
            e.getRowValue().setName(e.getNewValue());
            markDirty();
        });
        colImage.setCellFactory(TextFieldTableCell.forTableColumn());
        colImage.setOnEditCommit(e -> {
            e.getRowValue().setImageFilename(e.getNewValue());
            markDirty();
        });

        // Colonne actions : bouton Supprimer
        colAction.setCellFactory(new Callback<TableColumn<Category, Void>, TableCell<Category, Void>>() {
            @Override
            public TableCell<Category, Void> call(TableColumn<Category, Void> param) {
                return new TableCell<Category, Void>() {
                    private final Button btn = new Button("Supprimer");
                    {
                        btn.setOnAction(e -> {
                            Category c = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(c);
                            markDirty();
                        });
                        btn.getStyleClass().add("button-link");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });

        table.setItems(data);
    }

    private void loadData() throws Exception {
        data.clear();
        data.addAll(dao.findAll());
        btnSave.setDisable(true);
    }

    @FXML
    private void onAdd() {
        Category c = new Category();
        data.add(c);
        table.getSelectionModel().select(c);
        markDirty();
    }

    @FXML
    private void onSave() {
        data.forEach(c -> {
            try {
                if (c.getId() == 0)
                    dao.insert(c);
                else
                    dao.update(c);
            } catch (Exception e) {
            }
        });
        btnSave.setDisable(true);
    }

    private void markDirty() {
        btnSave.setDisable(false);
    }
}