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
        String sql = "SELECT id, user_id, client_id, model_id, motor_id, date_created, total_price FROM dbo.Devis";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            List<Devis> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Devis(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("client_id"),
                        rs.getObject("model_id", Integer.class),
                        rs.getObject("motor_id", Integer.class),
                        rs.getTimestamp("date_created").toLocalDateTime(),
                        rs.getObject("total_price", Integer.class)));
            }
            return list;
        }
    }

    @Override
    public Devis findById(int id) throws SQLException {
        String sql = "SELECT id, user_id, client_id, model_id, motor_id, date_created, total_price "
                + "FROM dbo.Devis WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Devis(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("client_id"),
                            rs.getObject("model_id", Integer.class),
                            rs.getObject("motor_id", Integer.class),
                            rs.getTimestamp("date_created").toLocalDateTime(),
                            rs.getObject("total_price", Integer.class));
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public void insert(Devis d) throws SQLException {
        String sql = "INSERT INTO dbo.Devis (user_id, client_id, model_id, motor_id, date_created, total_price) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getUserId());
            ps.setInt(2, d.getClientId());
            if (d.getModelId() != null)
                ps.setInt(3, d.getModelId());
            else
                ps.setNull(3, Types.INTEGER);
            if (d.getMotorId() != null)
                ps.setInt(4, d.getMotorId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.setTimestamp(5, Timestamp.valueOf(d.getDateCreated()));
            ps.setObject(6, d.getTotalPrice(), Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getInt(1));
                }
            }
        }
    }

    // src/main/java/fr/musclegarage/deviseur/dao/DevisDaoJdbc.java
    @Override
    public void update(Devis d) throws SQLException {
        String sql = "UPDATE dbo.Devis SET user_id=?, client_id=?, model_id=?, motor_id=?, date_created=?, total_price=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getUserId());
            ps.setInt(2, d.getClientId());
            if (d.getModelId() != null)
                ps.setInt(3, d.getModelId());
            else
                ps.setNull(3, Types.INTEGER);
            if (d.getMotorId() != null)
                ps.setInt(4, d.getMotorId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.setTimestamp(5, Timestamp.valueOf(d.getDateCreated()));
            ps.setObject(6, d.getTotalPrice(), Types.INTEGER);
            ps.setInt(7, d.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Devis WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
