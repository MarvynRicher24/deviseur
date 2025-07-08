package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Model;

public class ModelDaoJdbc implements ModelDao {
    private final Connection conn;

    public ModelDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Model> findAll() throws SQLException {
        String sql = "SELECT id, category_id, model_name, model_price, model_image FROM dbo.Model";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Model> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Model(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("model_name"),
                        rs.getInt("model_price"),
                        rs.getString("model_image")));
            }
            return list;
        }
    }

    @Override
    public Model findById(int id) throws SQLException {
        String sql = "SELECT id, category_id, model_name, model_price, model_image FROM dbo.Model WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Model(
                            rs.getInt("id"),
                            rs.getInt("category_id"),
                            rs.getString("model_name"),
                            rs.getInt("model_price"),
                            rs.getString("model_image"));
                }
            }
        }
        return null;
    }

    @Override
    public void insert(Model m) throws SQLException {
        String sql = "INSERT INTO dbo.Model (category_id, model_name, model_price, model_image) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getModelName());
            ps.setInt(3, m.getModelPrice());
            ps.setString(4, m.getModelImage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    m.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Model m) throws SQLException {
        String sql = "UPDATE dbo.Model SET category_id = ?, model_name = ?, model_price = ?, model_image = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getModelName());
            ps.setInt(3, m.getModelPrice());
            ps.setString(4, m.getModelImage());
            ps.setInt(5, m.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Model WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}