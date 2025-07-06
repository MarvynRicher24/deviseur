package fr.musclegarage.deviseur.ui;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.dao.OptionDao;
import fr.musclegarage.deviseur.dao.OptionDaoJdbc;
import fr.musclegarage.deviseur.model.Option;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminPanelOptionController {
    @FXML
    private VBox listContainer;
    @FXML
    private Button btnSave;

    private OptionDao optionDao;
    private final List<Option> items = new ArrayList<>();
    private final List<Option> deleted = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            optionDao = new OptionDaoJdbc(conn);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        listContainer.getChildren().clear();
        items.clear();
        items.addAll(optionDao.findAll());
        deleted.clear();
        btnSave.setDisable(true);
        for (Option o : items)
            listContainer.getChildren().add(createRow(o));
    }

    private HBox createRow(Option o) {
        HBox row = new HBox(10);
        row.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-alignment: CENTER_LEFT;");

        Label lblOpt = new Label("Option :");
        lblOpt.setPrefWidth(80);
        TextField tfOpt = new TextField(o.getOptionName());
        tfOpt.setPrefColumnCount(30);
        tfOpt.textProperty().addListener((obs, old, n) -> {
            o.setOptionName(n);
            markDirty();
        });

        Button btnDel = new Button("Supprimer");
        btnDel.setOnAction(e -> {
            listContainer.getChildren().remove(row);
            deleted.add(o);
            markDirty();
        });

        row.getChildren().addAll(lblOpt, tfOpt, btnDel);
        return row;
    }

    @FXML
    private void onAdd() {
        Option o = new Option();
        o.setOptionName("");
        items.add(o);
        listContainer.getChildren().add(createRow(o));
        markDirty();
    }

    @FXML
    private void onSave() {
        try {
            for (Option o : deleted)
                if (o.getId() != 0)
                    optionDao.delete(o.getId());
            for (Option o : items) {
                if (o.getId() == 0)
                    optionDao.insert(o);
                else
                    optionDao.update(o);
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