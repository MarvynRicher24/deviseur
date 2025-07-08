package fr.musclegarage.deviseur.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.CategoryDao;
import fr.musclegarage.deviseur.dao.CategoryDaoJdbc;
import fr.musclegarage.deviseur.dao.ClientDao;
import fr.musclegarage.deviseur.dao.ClientDaoJdbc;
import fr.musclegarage.deviseur.dao.DevisDao;
import fr.musclegarage.deviseur.dao.DevisDaoJdbc;
import fr.musclegarage.deviseur.dao.DevisOptionChoiceDao;
import fr.musclegarage.deviseur.dao.DevisOptionChoiceDaoJdbc;
import fr.musclegarage.deviseur.dao.ModelDao;
import fr.musclegarage.deviseur.dao.ModelDaoJdbc;
import fr.musclegarage.deviseur.dao.MotorDao;
import fr.musclegarage.deviseur.dao.MotorDaoJdbc;
import fr.musclegarage.deviseur.model.Category;
import fr.musclegarage.deviseur.model.Client;
import fr.musclegarage.deviseur.model.Devis;
import fr.musclegarage.deviseur.model.Model;
import fr.musclegarage.deviseur.model.Motor;
import fr.musclegarage.deviseur.model.OptionChoice;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserPanelDevisController {

    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<Devis> lvDevis;
    @FXML
    private Button btnDelete, btnEdit;
    @FXML
    private ImageView logoView;

    private DevisDao devisDao;
    private ClientDao clientDao;
    private DevisOptionChoiceDao docDao;
    private ModelDao modelDao;
    private CategoryDao categoryDao;
    private MotorDao motorDao;
    private ObservableList<Devis> masterList;

    @FXML
    public void initialize() {
        // 1) Charger le logo
        logoView.setImage(new Image(getClass().getResource("/logo.png").toString(), true));

        try {
            Connection conn = Database.getConnection();
            devisDao = new DevisDaoJdbc(conn);
            clientDao = new ClientDaoJdbc(conn);
            docDao = new DevisOptionChoiceDaoJdbc(conn);
            modelDao = new ModelDaoJdbc(conn);
            categoryDao = new CategoryDaoJdbc(conn);
            motorDao = new MotorDaoJdbc(conn);

            // 2) Charger tous les devis
            List<Devis> devis = devisDao.findAll();
            masterList = FXCollections.observableArrayList(devis);
            lvDevis.setItems(masterList);

            // 3) Rendu de chaque ligne => "n°ID – Nom Prénom"
            lvDevis.setCellFactory(lv -> new ListCell<Devis>() {
                @Override
                protected void updateItem(Devis d, boolean empty) {
                    super.updateItem(d, empty);
                    if (empty || d == null) {
                        setText(null);
                    } else {
                        String clientName = "Client inconnu";
                        try {
                            Client c = clientDao.findById(d.getClientId());
                            if (c != null) {
                                clientName = c.getClientName() + " " + c.getClientSurname();
                            }
                        } catch (SQLException ex) {
                            /* ignore */ }
                        setText("n°" + d.getId() + " – " + clientName);
                    }
                }
            });

            // 4) Activer/désactiver les boutons Modifier/Supprimer
            lvDevis.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
                boolean has = sel != null;
                btnDelete.setDisable(!has);
                btnEdit.setDisable(!has);
            });

            // 5) Recherche live
            txtSearch.textProperty().addListener((obs, old, text) -> {
                String lower = text.toLowerCase().trim();
                List<Devis> filtered = masterList.stream()
                        .filter(d -> {
                            try {
                                Client c = clientDao.findById(d.getClientId());
                                if (c != null) {
                                    String full = (c.getClientName() + " " + c.getClientSurname())
                                            .toLowerCase();
                                    return full.contains(lower);
                                }
                            } catch (SQLException ex) {
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                lvDevis.setItems(FXCollections.observableArrayList(filtered));
            });

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + e.getMessage()).showAndWait();
        }
    }

    /** Supprime le devis sélectionné */
    @FXML
    private void onDelete() {
        Devis sel = lvDevis.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;

        try {
            devisDao.delete(sel.getId());
            masterList.remove(sel);
            lvDevis.getItems().remove(sel);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de supprimer : " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Édite le devis: recharge *client*, *catégorie*, *modèle*, *moteur* et
     * *options*
     */
    @FXML
    private void onEdit() {
        Devis sel = lvDevis.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;

        try {
            // --- Client
            Client c = clientDao.findById(sel.getClientId());
            QuoteSession.setClient(c);

            // --- Options enregistrées
            List<OptionChoice> choices = docDao.findByDevisId(sel.getId());
            // S'il y a au moins un choix, on déduit modèle et catégorie
            if (!choices.isEmpty()) {
                OptionChoice first = choices.get(0);
                Model m = modelDao.findById(first.getModelId());
                QuoteSession.setModel(m);

                Category cat = categoryDao.findById(m.getCategoryId());
                QuoteSession.setCategory(cat);
            }

            // --- Moteur (si tu avais stocké motor_id dans Devis, sinon à adapter)
            // Motor motor = motorDao.findById(sel.getMotorId());
            // QuoteSession.setMotor(motor);

            // --- Recharger chaque option
            choices.forEach(oc -> QuoteSession.setChoice(oc.getOptionId(), oc));

            // --- Ouvre l'interface et va au récapitulatif
            App.showUserPanel();
            App.userBaseController.goToRecap();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de modifier le devis : " + e.getMessage()).showAndWait();
        }
    }

    /** Retour au menu principal */
    @FXML
    private void onGoMenu() {
        try {
            App.showMenu();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible de revenir au menu : " + e.getMessage()).showAndWait();
        }
    }
}
