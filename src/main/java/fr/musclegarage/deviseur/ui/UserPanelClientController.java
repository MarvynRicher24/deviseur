package fr.musclegarage.deviseur.ui;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.ClientDao;
import fr.musclegarage.deviseur.dao.ClientDaoJdbc;
import fr.musclegarage.deviseur.model.Client;
import fr.musclegarage.deviseur.model.QuoteSession;
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
            // si déjà en session, on pré-remplit
            Client c = QuoteSession.getClient();
            if (c != null) {
                txtName.setText(c.getClientName());
                txtSurname.setText(c.getClientSurname());
                txtAddress.setText(c.getClientAddress());
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "BD inaccessible: " + e.getMessage()).showAndWait();
        }
    }

    /** Sauvegarde en session et va à l’étape Catégorie */
    @FXML
    private void onNext() {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String address = txtAddress.getText().trim();
        if (name.isEmpty() || surname.isEmpty() || address.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Tous les champs sont requis.").showAndWait();
            return;
        }
        try {
            // On crée ou récupère la session
            Client c = QuoteSession.getClient();
            if (c == null) {
                c = new Client();
            }
            // On remplit les champs avant d'insérer / mettre à jour
            c.setClientName(name);
            c.setClientSurname(surname);
            c.setClientAddress(address);

            // Si c.getId()==0 alors c'est un nouveau client, sinon on devrait update
            if (c.getId() == 0) {
                clientDao.insert(c);
            } else {
                clientDao.update(c); // à implémenter dans le DAO
            }

            QuoteSession.setClient(c);
            App.userBaseController.unlockAndGoNext();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD: " + ex.getMessage()).showAndWait();
        }
    }
}
