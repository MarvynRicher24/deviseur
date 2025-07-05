package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;

import fr.musclegarage.deviseur.model.User;

public interface UserDao {
    void insert(User user) throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    boolean checkCredentials(String u, String hash) throws SQLException;

    boolean isAdmin(String username) throws SQLException;
}