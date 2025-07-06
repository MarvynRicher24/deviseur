package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.dao.ClientDao;
import fr.musclegarage.deviseur.dao.ClientDaoJdbc;
import fr.musclegarage.deviseur.model.Client;
import fr.musclegarage.deviseur.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UserPanelClientController {
    @FXML
    private TextField txtName, txtSurname, txtAddress;

    private ClientDao clientDao;

    @FXML
    public void initialize() {
        try {
            clientDao = new ClientDaoJdbc(Database.getConnection());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "BD inaccessible: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onSaveClient() {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String address = txtAddress.getText().trim();
        if (name.isEmpty() || surname.isEmpty() || address.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Tous les champs sont requis.").showAndWait();
            return;
        }
        try {
            Client c = new Client();
            c.setClientName(name);
            c.setClientSurname(surname);
            c.setClientAddress(address);
            clientDao.insert(c);
            // stocker c.getId() pour le devis en cours, puis passer à la page suivante
            // App.showUserPanelCategory(); // à implémenter
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD: " + ex.getMessage()).showAndWait();
        }
    }
}