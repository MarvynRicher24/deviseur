package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.OptionChoice;

public interface OptionChoiceDao {
    List<OptionChoice> findAll() throws SQLException;

    void insert(OptionChoice oc) throws SQLException;

    void update(OptionChoice oc) throws SQLException;

    void delete(int id) throws SQLException;
}