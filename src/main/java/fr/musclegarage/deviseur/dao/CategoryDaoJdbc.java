package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Category;

public class CategoryDaoJdbc implements CategoryDao {
    private final Connection conn;

    public CategoryDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT id, name, image_filename FROM dbo.Category";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("image_filename")));
            }
            return list;
        }
    }

    @Override
    public void insert(Category c) throws SQLException {
        String sql = "INSERT INTO dbo.Category (name, image_filename) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getImageFilename());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    c.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Category c) throws SQLException {
        String sql = "UPDATE dbo.Category SET name = ?, image_filename = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getImageFilename());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Category WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}