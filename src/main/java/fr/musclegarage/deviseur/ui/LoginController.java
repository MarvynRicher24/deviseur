package fr.musclegarage.deviseur.ui;

import java.sql.Connection;
import java.sql.SQLException;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.UserDao;
import fr.musclegarage.deviseur.dao.UserDaoJdbc;
import fr.musclegarage.deviseur.util.Database;
import fr.musclegarage.deviseur.util.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    private UserDao userDao;

    @FXML
    public void initialize() {
        try {
            Connection conn = Database.getConnection();
            userDao = new UserDaoJdbc(conn);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "BD inaccessible: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onLogin() {
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText();
        if (u.isEmpty() || p.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Tous les champs sont requis.").showAndWait();
            return;
        }
        try {
            String hash = PasswordUtils.hash(p);
            if (!userDao.checkCredentials(u, hash)) {
                new Alert(Alert.AlertType.ERROR, "Identifiants incorrects.").showAndWait();
                return;
            }
            if (userDao.isAdmin(u)) {
                App.showAdminPanelOne();
            } else {
                App.showMenu();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur BD : " + e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur interne : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onGoRegister() {
        try {
            App.showRegistration();
        } catch (Exception e) {
        }
    }
}