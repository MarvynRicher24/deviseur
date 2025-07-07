package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Client;

public class ClientDaoJdbc implements ClientDao {
    private final Connection conn;

    public ClientDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT id, client_name, client_surname, client_address FROM dbo.Client";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Client(
                        rs.getInt("id"),
                        rs.getString("client_name"),
                        rs.getString("client_surname"),
                        rs.getString("client_address")));
            }
        }
        return list;
    }

    @Override
    public Client findById(int id) throws SQLException {
        String sql = "SELECT client_name, client_surname, client_address FROM dbo.Client WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Client(id,
                            rs.getString("client_name"),
                            rs.getString("client_surname"),
                            rs.getString("client_address"));
                }
            }
        }
        return null;
    }

    @Override
    public void insert(Client c) throws SQLException {
        String sql = "INSERT INTO dbo.Client (client_name, client_surname, client_address) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getClientName());
            ps.setString(2, c.getClientSurname());
            ps.setString(3, c.getClientAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    c.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Client c) throws SQLException {
        String sql = "UPDATE dbo.Client SET client_name = ?, client_surname = ?, client_address = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getClientName());
            ps.setString(2, c.getClientSurname());
            ps.setString(3, c.getClientAddress());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }
}