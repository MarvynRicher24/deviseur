package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.OptionChoice;

public class OptionChoiceDaoJdbc implements OptionChoiceDao {
    private final Connection conn;

    public OptionChoiceDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<OptionChoice> findAll() throws SQLException {
        String sql = "SELECT id, model_id, option_id, optionChoice_name, optionChoice_price, optionChoice_image FROM dbo.OptionChoice";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<OptionChoice> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new OptionChoice(
                        rs.getInt("id"),
                        rs.getInt("model_id"),
                        rs.getInt("option_id"),
                        rs.getString("optionChoice_name"),
                        rs.getInt("optionChoice_price"),
                        rs.getString("optionChoice_image")));
            }
            return list;
        }
    }

    @Override
    public void insert(OptionChoice oc) throws SQLException {
        String sql = "INSERT INTO dbo.OptionChoice (model_id, option_id, optionChoice_name, optionChoice_price, optionChoice_image) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, oc.getModelId());
            ps.setInt(2, oc.getOptionId());
            ps.setString(3, oc.getOptionChoiceName());
            ps.setInt(4, oc.getOptionChoicePrice());
            ps.setString(5, oc.getOptionChoiceImage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    oc.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(OptionChoice oc) throws SQLException {
        String sql = "UPDATE dbo.OptionChoice SET model_id=?, option_id=?, optionChoice_name=?, optionChoice_price=?, optionChoice_image=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oc.getModelId());
            ps.setInt(2, oc.getOptionId());
            ps.setString(3, oc.getOptionChoiceName());
            ps.setInt(4, oc.getOptionChoicePrice());
            ps.setString(5, oc.getOptionChoiceImage());
            ps.setInt(6, oc.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.OptionChoice WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}