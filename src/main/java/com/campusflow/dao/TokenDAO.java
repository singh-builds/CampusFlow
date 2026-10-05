package com.campusflow.dao;

import com.campusflow.model.Token;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TokenDAO {
    private static final String SELECT =
        "SELECT t.*, s.full_name AS student_name, sv.service_name FROM tokens t " +
        "JOIN students s ON s.student_id = t.student_id JOIN services sv ON sv.service_id = t.service_id ";

    private Token map(ResultSet rs) throws SQLException {
        return new Token(rs.getInt("token_id"), rs.getString("token_number"), rs.getInt("student_id"), rs.getString("student_name"),
                rs.getInt("service_id"), rs.getString("service_name"), rs.getString("status"),
                rs.getTimestamp("created_at"), rs.getTimestamp("called_at"), rs.getTimestamp("completed_at"));
    }

    private List<Token> list(PreparedStatement ps) throws SQLException {
        List<Token> out = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        return out;
    }

    public int countForService(Connection c, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM tokens WHERE service_id = ?")) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public boolean hasActiveToken(Connection c, int studentId, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM tokens WHERE student_id = ? AND service_id = ? AND status IN ('WAITING','CALLED') LIMIT 1")) {
            ps.setInt(1, studentId); ps.setInt(2, serviceId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public int insert(Connection c, String number, int studentId, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tokens (token_number, student_id, service_id) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, number); ps.setInt(2, studentId); ps.setInt(3, serviceId);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public Token findById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.token_id = ?")) {
            ps.setInt(1, id);
            List<Token> l = list(ps); return l.isEmpty() ? null : l.get(0);
        }
    }

    public Token findByIdForUpdate(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.token_id = ? FOR UPDATE")) {
            ps.setInt(1, id);
            List<Token> l = list(ps); return l.isEmpty() ? null : l.get(0);
        }
    }

    public Token findByNumber(Connection c, String number) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.token_number = ? ORDER BY t.token_id DESC LIMIT 1")) {
            ps.setString(1, number);
            List<Token> l = list(ps); return l.isEmpty() ? null : l.get(0);
        }
    }

    public List<Token> findByStudent(Connection c, int studentId, boolean onlyActive) throws SQLException {
        String extra = onlyActive ? "AND t.status IN ('WAITING','CALLED') " : "";
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.student_id = ? " + extra + "ORDER BY t.token_id DESC")) {
            ps.setInt(1, studentId);
            return list(ps);
        }
    }

    public List<Token> findWaiting(Connection c, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.service_id = ? AND t.status = 'WAITING' ORDER BY t.token_id")) {
            ps.setInt(1, serviceId);
            return list(ps);
        }
    }

    /** Locks the oldest waiting token so two staff members cannot call the same token. */
    public Token findNextWaitingForUpdate(Connection c, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                SELECT + "WHERE t.service_id = ? AND t.status = 'WAITING' ORDER BY t.token_id LIMIT 1 FOR UPDATE")) {
            ps.setInt(1, serviceId);
            List<Token> l = list(ps); return l.isEmpty() ? null : l.get(0);
        }
    }

    public Token findCalled(Connection c, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                SELECT + "WHERE t.service_id = ? AND t.status = 'CALLED' ORDER BY t.called_at DESC LIMIT 1")) {
            ps.setInt(1, serviceId);
            List<Token> l = list(ps); return l.isEmpty() ? null : l.get(0);
        }
    }

    public int waitingCount(Connection c, int serviceId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM tokens WHERE service_id = ? AND status = 'WAITING'")) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    /** Position in line = number of WAITING tokens of the same service that are older or equal. */
    public int position(Connection c, int serviceId, int tokenId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM tokens WHERE service_id = ? AND status = 'WAITING' AND token_id <= ?")) {
            ps.setInt(1, serviceId); ps.setInt(2, tokenId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public void updateStatus(Connection c, int tokenId, String status, Integer staffId) throws SQLException {
        String sql = switch (status) {
            case "CALLED" -> "UPDATE tokens SET status = ?, handled_by = ?, called_at = NOW() WHERE token_id = ?";
            case "COMPLETED", "SKIPPED" -> "UPDATE tokens SET status = ?, handled_by = ?, completed_at = NOW() WHERE token_id = ?";
            default -> "UPDATE tokens SET status = ?, handled_by = ? WHERE token_id = ?";
        };
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            if (staffId == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, staffId);
            ps.setInt(3, tokenId);
            ps.executeUpdate();
        }
    }

    public Map<String, Integer> countByStatus(Connection c) throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String s : new String[]{"WAITING", "CALLED", "COMPLETED", "SKIPPED", "CANCELLED"}) m.put(s, 0);
        try (PreparedStatement ps = c.prepareStatement("SELECT status, COUNT(*) FROM tokens GROUP BY status"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }
}
