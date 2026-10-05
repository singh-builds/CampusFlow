package com.campusflow.service;

import com.campusflow.dao.*;
import com.campusflow.model.*;
import com.campusflow.util.AppException;
import com.campusflow.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Admin features + public service list. CRUD on services, staff and students. */
public class AdminService {
    private final ServiceDAO serviceDao = new ServiceDAO();
    private final StudentDAO studentDao = new StudentDAO();
    private final StaffDAO staffDao = new StaffDAO();
    private final TokenDAO tokenDao = new TokenDAO();
    private final HistoryDAO historyDao = new HistoryDAO();

    /** Services with the current number of waiting students (for cards and REST). */
    public List<Service> services(boolean onlyActive) {
        try (Connection c = DBConnection.getConnection()) {
            List<Service> list = serviceDao.findAll(c, onlyActive);
            for (Service s : list) s.setWaiting(tokenDao.waitingCount(c, s.getId()));
            return list;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public void createService(String name, String prefix, String description) {
        name = AuthService.need(name, "Service name");
        prefix = AuthService.need(prefix, "Token prefix").toUpperCase();
        if (!prefix.matches("[A-Z]{2,5}")) throw new AppException("Token prefix must be 2 to 5 letters, for example EX.");
        try (Connection c = DBConnection.getConnection()) {
            serviceDao.insert(c, name, prefix, description == null ? null : description.trim());
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AppException("A service with this name or prefix already exists.");
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public void setServiceActive(int id, boolean active) {
        try (Connection c = DBConnection.getConnection()) { serviceDao.setActive(c, id, active); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<Student> students() {
        try (Connection c = DBConnection.getConnection()) { return studentDao.findAll(c); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public void setStudentStatus(int id, String status) {
        try (Connection c = DBConnection.getConnection()) { studentDao.setStatus(c, id, status); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<Staff> staff() {
        try (Connection c = DBConnection.getConnection()) { return staffDao.findAll(c); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public void setStaffStatus(int id, String status) {
        try (Connection c = DBConnection.getConnection()) { staffDao.setStatus(c, id, status); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<HistoryRow> recentActivity() {
        try (Connection c = DBConnection.getConnection()) { return historyDao.findRecent(c, 100); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    /** Numbers for the admin dashboard cards. */
    public Map<String, Object> stats() {
        try (Connection c = DBConnection.getConnection()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("students", studentDao.count(c));
            m.put("staff", staffDao.count(c));
            m.put("services", serviceDao.count(c));
            m.put("tokens", tokenDao.countByStatus(c));
            return m;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }
}
