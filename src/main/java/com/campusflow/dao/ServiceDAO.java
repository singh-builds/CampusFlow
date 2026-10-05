package com.campusflow.dao;

import com.campusflow.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO = only SQL for the `services` table. The caller (service layer) owns the Connection/transaction. */
public class ServiceDAO {
    private Service map(ResultSet rs) throws SQLException {
        return new Service(rs.getInt("service_id"), rs.getString("service_name"), rs.getString("token_prefix"),
                rs.getString("description"), rs.getBoolean("is_active"), rs.getTimestamp("created_at"));
    }

    public List<Service> findAll(Connection c, boolean onlyActive) throws SQLException {
        String sql = "SELECT * FROM services" + (onlyActive ? " WHERE is_active = TRUE" : "") + " ORDER BY service_name";
        List<Service> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Service findById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM services WHERE service_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    /** Locks the service row until the transaction ends, so two students cannot get the same token number. */
    public Service findByIdForUpdate(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM services WHERE service_id = ? FOR UPDATE")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public int insert(Connection c, String name, String prefix, String description) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO services (service_name, token_prefix, description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, prefix); ps.setString(3, description);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public void setActive(Connection c, int id, boolean active) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE services SET is_active = ? WHERE service_id = ?")) {
            ps.setBoolean(1, active); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    public int count(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM services"); ResultSet rs = ps.executeQuery()) {
            rs.next(); return rs.getInt(1);
        }
    }
}
