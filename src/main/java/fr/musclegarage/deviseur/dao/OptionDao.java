package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Option;

public interface OptionDao {
    List<Option> findAll() throws SQLException;

    void insert(Option o) throws SQLException;

    void update(Option o) throws SQLException;

    void delete(int id) throws SQLException;
}