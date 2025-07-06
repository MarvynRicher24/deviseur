package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Option;

public class OptionDaoJdbc implements OptionDao {
    private final Connection conn;

    public OptionDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Option> findAll() throws SQLException {
        String sql = "SELECT id, option_name FROM dbo.[Option]";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Option> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Option(
                        rs.getInt("id"),
                        rs.getString("option_name")));
            }
            return list;
        }
    }

    @Override
    public void insert(Option o) throws SQLException {
        String sql = "INSERT INTO dbo.[Option] (option_name) VALUES (? )";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getOptionName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    o.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Option o) throws SQLException {
        String sql = "UPDATE dbo.[Option] SET option_name = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, o.getOptionName());
            ps.setInt(2, o.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.[Option] WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}