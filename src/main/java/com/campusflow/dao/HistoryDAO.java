package com.campusflow.dao;

import com.campusflow.model.HistoryRow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    private static final String SELECT =
        "SELECT h.history_id, h.action, h.action_time, h.remarks, t.token_number, sv.service_name, " +
        "s.full_name AS student_name, st.full_name AS staff_name FROM queue_history h " +
        "JOIN tokens t ON t.token_id = h.token_id JOIN services sv ON sv.service_id = t.service_id " +
        "JOIN students s ON s.student_id = t.student_id LEFT JOIN staff st ON st.staff_id = h.staff_id ";

    public void insert(Connection c, int tokenId, Integer staffId, String action, String remarks) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO queue_history (token_id, staff_id, action, remarks) VALUES (?,?,?,?)")) {
            ps.setInt(1, tokenId);
            if (staffId == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, staffId);
            ps.setString(3, action); ps.setString(4, remarks);
            ps.executeUpdate();
        }
    }

    private List<HistoryRow> run(PreparedStatement ps) throws SQLException {
        List<HistoryRow> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new HistoryRow(rs.getInt("history_id"), rs.getString("token_number"), rs.getString("service_name"),
                    rs.getString("student_name"), rs.getString("staff_name"), rs.getString("action"), rs.getTimestamp("action_time"), rs.getString("remarks")));
        }
        return list;
    }

    public List<HistoryRow> findByService(Connection c, int serviceId, int limit) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "WHERE t.service_id = ? ORDER BY h.history_id DESC LIMIT ?")) {
            ps.setInt(1, serviceId); ps.setInt(2, limit);
            return run(ps);
        }
    }

    public List<HistoryRow> findRecent(Connection c, int limit) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY h.history_id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            return run(ps);
        }
    }
}
