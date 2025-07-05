package fr.musclegarage.deviseur.ui;

import java.sql.Connection;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.UserDao;
import fr.musclegarage.deviseur.dao.UserDaoJdbc;
import fr.musclegarage.deviseur.model.User;
import fr.musclegarage.deviseur.util.Database;
import fr.musclegarage.deviseur.util.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {
    @FXML
    private TextField txtUsername, txtEmail;
    @FXML
    private PasswordField txtPassword, txtPassword2;
    @FXML
    private Button btnRegister;

    private UserDao userDao;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            userDao = new UserDaoJdbc(conn);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Impossible de se connecter à la BD : " + e.getMessage());
            btnRegister.setDisable(true);
        }
    }

    @FXML
    public void onRegister() {
        String u = txtUsername.getText().trim();
        String e = txtEmail.getText().trim();
        String p = txtPassword.getText();
        String p2 = txtPassword2.getText();

        if (u.isEmpty() || e.isEmpty() || p.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont requis.");
            return;
        }
        if (!p.equals(p2)) {
            showAlert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas.");
            return;
        }
        try {
            if (userDao.existsByUsername(u)) {
                showAlert(Alert.AlertType.ERROR, "Nom d’utilisateur déjà pris.");
                return;
            }
            if (userDao.existsByEmail(e)) {
                showAlert(Alert.AlertType.ERROR, "Email déjà utilisé.");
                return;
            }
            String hash = PasswordUtils.hash(p);
            userDao.insert(new User(u, hash, e));
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie !");
            App.showLogin();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage());
        }
    }

    @FXML
    public void onGoLogin() {
        try {
            App.showLogin();
        } catch (Exception e) {
        }
    }

    private void showAlert(Alert.AlertType t, String m) {
        new Alert(t, m).showAndWait();
    }
}
