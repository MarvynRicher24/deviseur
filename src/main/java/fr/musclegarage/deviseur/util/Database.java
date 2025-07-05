package fr.musclegarage.deviseur.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Database {
    private static Connection conn;

    /** Retourne une connexion singleton chargée depuis config.properties */
    public static Connection getConnection() throws Exception {
        if (conn == null || conn.isClosed()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("src/main/resources/config.properties")) {
                props.load(in);
            }
            String url = props.getProperty("db.url");
            String usr = props.getProperty("db.user");
            String pwd = props.getProperty("db.password");
            conn = DriverManager.getConnection(url, usr, pwd);
        }
        return conn;
    }
}