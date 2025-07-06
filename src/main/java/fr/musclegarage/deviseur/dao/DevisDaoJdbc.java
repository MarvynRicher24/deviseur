package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Devis;

public class DevisDaoJdbc implements DevisDao {
    private final Connection conn;

    public DevisDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Devis> findAll() throws SQLException {
        List<Devis> list = new ArrayList<>();
        String sql = "SELECT id, user_id, client_id, date_created, total_price FROM dbo.Devis";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Devis(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("client_id"),
                        rs.getTimestamp("date_created").toLocalDateTime(),
                        rs.getObject("total_price", Integer.class)));
            }
        }
        return list;
    }

    @Override
    public void insert(Devis d) throws SQLException {
        String sql = "INSERT INTO dbo.Devis (user_id, client_id, date_created, total_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getUserId());
            ps.setInt(2, d.getClientId());
            ps.setTimestamp(3, Timestamp.valueOf(d.getDateCreated()));
            ps.setObject(4, d.getTotalPrice(), Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    d.setId(keys.getInt(1));
            }
        }
    }
}