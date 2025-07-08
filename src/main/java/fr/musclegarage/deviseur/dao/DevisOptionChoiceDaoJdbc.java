package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.OptionChoice;

public class DevisOptionChoiceDaoJdbc implements DevisOptionChoiceDao {
    private final Connection conn;

    public DevisOptionChoiceDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<OptionChoice> findByDevisId(int id) throws SQLException {
        String sql = "SELECT oc.* FROM dbo.DevisOptionChoice doc "
                + "JOIN dbo.OptionChoice oc ON doc.option_choice_id = oc.id "
                + "WHERE doc.devis_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
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
    }
}
