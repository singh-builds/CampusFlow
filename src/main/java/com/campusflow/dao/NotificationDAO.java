package com.campusflow.dao;

import com.campusflow.model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public void insert(Connection c, int studentId, Integer tokenId, String message) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO notifications (student_id, token_id, message) VALUES (?,?,?)")) {
            ps.setInt(1, studentId);
            if (tokenId == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, tokenId);
            ps.setString(3, message);
            ps.executeUpdate();
        }
    }

    public List<Notification> findByStudent(Connection c, int studentId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM notifications WHERE student_id = ? ORDER BY notification_id DESC LIMIT 100")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Notification(rs.getInt("notification_id"), rs.getString("message"),
                        rs.getBoolean("is_read"), rs.getTimestamp("created_at")));
            }
        }
        return list;
    }

    public int unreadCount(Connection c, int studentId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM notifications WHERE student_id = ? AND is_read = FALSE")) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public void markAllRead(Connection c, int studentId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE notifications SET is_read = TRUE WHERE student_id = ? AND is_read = FALSE")) {
            ps.setInt(1, studentId); ps.executeUpdate();
        }
    }
}
