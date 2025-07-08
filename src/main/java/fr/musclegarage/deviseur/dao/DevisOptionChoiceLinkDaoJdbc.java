package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DevisOptionChoiceLinkDaoJdbc implements DevisOptionChoiceLinkDao {
    private final Connection conn;

    public DevisOptionChoiceLinkDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertLink(int devisId, int optionChoiceId) throws SQLException {
        String sql = "INSERT INTO dbo.DevisOptionChoice (devis_id, option_choice_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, devisId);
            ps.setInt(2, optionChoiceId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteByDevisId(int devisId) throws SQLException {
        String sql = "DELETE FROM dbo.DevisOptionChoice WHERE devis_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, devisId);
            ps.executeUpdate();
        }
    }
}
