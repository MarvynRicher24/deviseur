package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Devis;

public interface DevisDao {
    List<Devis> findAll() throws SQLException;

    Devis findById(int id) throws SQLException;

    void insert(Devis d) throws SQLException;

    void update(Devis d) throws SQLException;

    void delete(int id) throws SQLException;
}
