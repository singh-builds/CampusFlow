package com.campusflow.dao;

import com.campusflow.model.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private static final String SELECT =
        "SELECT st.*, sv.service_name FROM staff st LEFT JOIN services sv ON sv.service_id = st.service_id ";

    private Staff map(ResultSet rs) throws SQLException {
        int sid = rs.getInt("service_id");
        Integer serviceId = rs.wasNull() ? null : sid;
        return new Staff(rs.getInt("staff_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("password_hash"),
                rs.getString("role"), serviceId, rs.getString("service_name"), rs.getString("status"), rs.getTimestamp("created_at"));
    }

    public int insert(Connection c, String name, String email, String hash, String role, Integer serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO staff (full_name, email, password_hash, role, service_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, hash); ps.setString(4, role);
            if (serviceId == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, serviceId);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public Staff findByEmail(Connection c, String email) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE st.email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Staff findById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE st.staff_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Staff> findAll(Connection c) throws SQLException {
        List<Staff> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY st.created_at DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int countByRole(Connection c, String role) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM staff WHERE role = ?")) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public int count(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM staff"); ResultSet rs = ps.executeQuery()) {
            rs.next(); return rs.getInt(1);
        }
    }

    public void setStatus(Connection c, int id, String status) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE staff SET status = ? WHERE staff_id = ?")) {
            ps.setString(1, status); ps.setInt(2, id); ps.executeUpdate();
        }
    }
}
