package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Devis;

public interface DevisDao {
    List findAll() throws SQLException;

    void insert(Devis d) throws SQLException;
}