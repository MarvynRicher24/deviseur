package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Model;

public interface ModelDao {
    List<Model> findAll() throws SQLException;

    void insert(Model m) throws SQLException;

    void update(Model m) throws SQLException;

    void delete(int id) throws SQLException;
}