package fr.musclegarage.deviseur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;

    //Première page qui s'ouvre
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static void showRegistration() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/registration.fxml"));
        Scene scene = new Scene(loader.load(), 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Deviseur - Inscription");
        primaryStage.show();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Deviseur - Connexion");
        primaryStage.show();
    }

    public static void showMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/menu.fxml"));
        Scene scene = new Scene(loader.load(), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Deviseur - Menu");
        primaryStage.show();
    }

    public static void showAdminPanelOne() throws Exception {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/adminPanelOne.fxml"));
    Scene scene = new Scene(loader.load(), 600, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Deviseur - Admin");
        primaryStage.show();
}


    public static void main(String[] args) {
        launch();
    }
}
