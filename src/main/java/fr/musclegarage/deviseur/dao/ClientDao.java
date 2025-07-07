package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Client;

public interface ClientDao {
    List findAll() throws SQLException;

    Client findById(int id) throws SQLException;

    void insert(Client c) throws SQLException;

    void update(Client c) throws SQLException;
}