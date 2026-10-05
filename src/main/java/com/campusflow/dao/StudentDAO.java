package com.campusflow.dao;

import com.campusflow.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("student_id"), rs.getString("roll_no"), rs.getString("full_name"), rs.getString("email"),
                rs.getString("phone"), rs.getString("department"), rs.getString("status"), rs.getString("password_hash"),
                rs.getTimestamp("created_at"));
    }

    public int insert(Connection c, String rollNo, String name, String email, String phone, String dept, String hash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO students (roll_no, full_name, email, phone, department, password_hash) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rollNo); ps.setString(2, name); ps.setString(3, email);
            ps.setString(4, phone); ps.setString(5, dept); ps.setString(6, hash);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public Student findByEmail(Connection c, String email) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM students WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public Student findById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM students WHERE student_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Student> findAll(Connection c) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM students ORDER BY created_at DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void setStatus(Connection c, int id, String status) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE students SET status = ? WHERE student_id = ?")) {
            ps.setString(1, status); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    public int count(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM students"); ResultSet rs = ps.executeQuery()) {
            rs.next(); return rs.getInt(1);
        }
    }
}
