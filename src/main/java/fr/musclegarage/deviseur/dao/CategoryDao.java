package fr.musclegarage.deviseur.dao;

import java.sql.SQLException;
import java.util.List;

import fr.musclegarage.deviseur.model.Category;

public interface CategoryDao {
    List<Category> findAll() throws SQLException;
    void insert(Category c) throws SQLException;
    void update(Category c) throws SQLException;
    void delete(int id) throws SQLException;
}