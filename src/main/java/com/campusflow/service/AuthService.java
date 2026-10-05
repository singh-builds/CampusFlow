package com.campusflow.service;

import com.campusflow.dao.StaffDAO;
import com.campusflow.dao.StudentDAO;
import com.campusflow.model.Staff;
import com.campusflow.model.Student;
import com.campusflow.util.AppException;
import com.campusflow.util.DBConnection;
import com.campusflow.util.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/** Business logic for registration and login. Validates input, hashes passwords, calls the DAOs. */
public class AuthService {
    private final StudentDAO studentDao = new StudentDAO();
    private final StaffDAO staffDao = new StaffDAO();

    static String need(String value, String label) {
        if (value == null || value.trim().isEmpty()) throw new AppException(label + " is required.");
        return value.trim();
    }

    static void checkEmail(String email) {
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new AppException("Please enter a valid email address.");
    }

    static void checkPassword(String pw) {
        if (pw == null || pw.length() < 8) throw new AppException("Password must be at least 8 characters.");
    }

    public void registerStudent(String rollNo, String name, String email, String phone, String dept, String password) {
        rollNo = need(rollNo, "Roll number"); name = need(name, "Full name"); email = need(email, "Email").toLowerCase();
        checkEmail(email); checkPassword(password);
        try (Connection c = DBConnection.getConnection()) {
            studentDao.insert(c, rollNo, name, email, phone == null ? null : phone.trim(), dept == null ? null : dept.trim(),
                    PasswordUtil.hash(password));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AppException("A student with this roll number or email already exists.");
        } catch (SQLException e) {
            throw new AppException("Database error while registering.", e);
        }
    }

    /** Returns the student when email+password are right, otherwise null. */
    public Student loginStudent(String email, String password) {
        if (email == null || password == null) return null;
        try (Connection c = DBConnection.getConnection()) {
            Student s = studentDao.findByEmail(c, email.trim().toLowerCase());
            if (s != null && "ACTIVE".equals(s.getStatus()) && PasswordUtil.verify(password, s.getPasswordHash())) return s;
            return null;
        } catch (SQLException e) {
            throw new AppException("Database error while logging in.", e);
        }
    }

    public Staff loginStaff(String email, String password) {
        if (email == null || password == null) return null;
        try (Connection c = DBConnection.getConnection()) {
            Staff s = staffDao.findByEmail(c, email.trim().toLowerCase());
            if (s != null && "ACTIVE".equals(s.getStatus()) && PasswordUtil.verify(password, s.getPasswordHash())) return s;
            return null;
        } catch (SQLException e) {
            throw new AppException("Database error while logging in.", e);
        }
    }

    /** True while the system has no administrator yet (fresh database). */
    public boolean setupNeeded() {
        try (Connection c = DBConnection.getConnection()) {
            return staffDao.countByRole(c, "ADMIN") == 0;
        } catch (SQLException e) {
            throw new AppException("Cannot reach the database. Check config/db.properties and that MySQL is running.", e);
        }
    }

    /** Creates the first administrator from the details typed on the /setup page. Works only once. */
    public synchronized void createFirstAdmin(String name, String email, String password) {
        if (!setupNeeded()) throw new AppException("Setup is already done.");
        createStaff(name, email, password, "ADMIN", null);
    }

    public void createStaff(String name, String email, String password, String role, Integer serviceId) {
        name = need(name, "Full name"); email = need(email, "Email").toLowerCase();
        checkEmail(email); checkPassword(password);
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) throw new AppException("Invalid role.");
        if ("STAFF".equals(role) && serviceId == null) throw new AppException("Please choose the service this staff member handles.");
        try (Connection c = DBConnection.getConnection()) {
            staffDao.insert(c, name, email, PasswordUtil.hash(password), role, serviceId);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AppException("A staff member with this email already exists.");
        } catch (SQLException e) {
            throw new AppException("Database error while creating staff.", e);
        }
    }
}
