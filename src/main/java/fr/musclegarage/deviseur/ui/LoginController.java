package fr.musclegarage.deviseur.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fr.musclegarage.deviseur.App;
import fr.musclegarage.deviseur.dao.UserDao;
import fr.musclegarage.deviseur.dao.UserDaoJdbc;
import fr.musclegarage.deviseur.model.QuoteSession;
import fr.musclegarage.deviseur.model.User;
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
            new Alert(Alert.AlertType.ERROR, "BD inaccessible : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Tous les champs sont requis.").showAndWait();
            return;
        }

        try {
            String hash = PasswordUtils.hash(password);
            if (!userDao.checkCredentials(username, hash)) {
                new Alert(Alert.AlertType.ERROR, "Identifiants incorrects.").showAndWait();
                return;
            }

            // Récupérer l'utilisateur complet
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, username, password_hash, email, created_at FROM dbo.Users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at").toLocalDateTime());
                QuoteSession.setUser(u);
            }

            // Navigation
            if (userDao.isAdmin(username)) {
                App.showAdminPanelOne();
            } else {
                App.showMenu();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onGoRegister() {
        try {
            App.showRegistration();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Impossible d’ouvrir l’inscription : " + e.getMessage())
                    .showAndWait();
        }
    }
}