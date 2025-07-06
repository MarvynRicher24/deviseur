package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Motor;

public interface MotorDao {
    List<Motor> findAll() throws SQLException;

    void insert(Motor m) throws SQLException;

    void update(Motor m) throws SQLException;

    void delete(int id) throws SQLException;
}