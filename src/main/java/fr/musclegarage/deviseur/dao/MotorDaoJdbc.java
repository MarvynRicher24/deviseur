package fr.musclegarage.deviseur.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.musclegarage.deviseur.model.Motor;

public class MotorDaoJdbc implements MotorDao {
    private final Connection conn;

    public MotorDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Motor> findAll() throws SQLException {
        String sql = "SELECT id, category_id, motor_name, motor_price, ch_motor, torque_motor, motor_image FROM dbo.Motor";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            List<Motor> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Motor(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("motor_name"),
                        rs.getInt("motor_price"),
                        rs.getInt("ch_motor"),
                        rs.getInt("torque_motor"),
                        rs.getString("motor_image")));
            }
            return list;
        }
    }

    @Override
    public void insert(Motor m) throws SQLException {
        String sql = "INSERT INTO dbo.Motor (category_id, motor_name, motor_price, ch_motor, torque_motor, motor_image) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getMotorName());
            ps.setInt(3, m.getMotorPrice());
            ps.setInt(4, m.getChMotor());
            ps.setInt(5, m.getTorqueMotor());
            ps.setString(6, m.getMotorImage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    m.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void update(Motor m) throws SQLException {
        String sql = "UPDATE dbo.Motor SET category_id=?, motor_name=?, motor_price=?, ch_motor=?, torque_motor=?, motor_image=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getCategoryId());
            ps.setString(2, m.getMotorName());
            ps.setInt(3, m.getMotorPrice());
            ps.setInt(4, m.getChMotor());
            ps.setInt(5, m.getTorqueMotor());
            ps.setString(6, m.getMotorImage());
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dbo.Motor WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}