package com.campusflow.service;

import com.campusflow.dao.*;
import com.campusflow.model.*;
import com.campusflow.util.AppException;
import com.campusflow.util.DBConnection;
import com.campusflow.util.MailUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core queue logic. Every method that changes data is ONE database transaction:
 * commit when all steps work, rollback when anything fails.
 * Synchronization: one Java lock per service (inside this JVM) + a MySQL row lock (FOR UPDATE).
 */
public class TokenService {
    private static final ConcurrentHashMap<Integer, Object> LOCKS = new ConcurrentHashMap<>();

    private final ServiceDAO serviceDao = new ServiceDAO();
    private final StudentDAO studentDao = new StudentDAO();
    private final TokenDAO tokenDao = new TokenDAO();
    private final HistoryDAO historyDao = new HistoryDAO();
    private final NotificationDAO notificationDao = new NotificationDAO();

    private static Object lockFor(int serviceId) { return LOCKS.computeIfAbsent(serviceId, k -> new Object()); }

    private interface Work<T> { T run(Connection c) throws SQLException; }

    private <T> T inTransaction(Work<T> work) {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (RuntimeException | SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new AppException("Database error. Please try again.", e);
        }
    }

    private void notifyStudent(Connection c, int studentId, Integer tokenId, String message) throws SQLException {
        notificationDao.insert(c, studentId, tokenId, message);
        Student s = studentDao.findById(c, studentId);
        if (s != null) MailUtil.sendAsync(s.getEmail(), "CampusFlow update", message); // JavaMail (only if enabled)
    }

    // ---------------- student actions ----------------

    public Token requestToken(int studentId, int serviceId) {
        synchronized (lockFor(serviceId)) {
            return inTransaction(c -> {
                Service sv = serviceDao.findByIdForUpdate(c, serviceId);           // row lock
                if (sv == null || !sv.isActive()) throw new AppException("This service is not available.");
                if (tokenDao.hasActiveToken(c, studentId, serviceId))
                    throw new AppException("You already have an active token for " + sv.getName() + ".");
                String number = sv.getPrefix() + "-" + (101 + tokenDao.countForService(c, serviceId));
                int id = tokenDao.insert(c, number, studentId, serviceId);
                historyDao.insert(c, id, null, "CREATED", "Token requested by student");
                int pos = tokenDao.position(c, serviceId, id);
                notifyStudent(c, studentId, id, "Your token " + number + " for " + sv.getName() + " is created. Position in line: " + pos + ".");
                return tokenDao.findById(c, id);
            });
        }
    }

    public void cancelToken(int studentId, int tokenId) {
        inTransaction(c -> {
            Token t = tokenDao.findByIdForUpdate(c, tokenId);
            if (t == null || t.getStudentId() != studentId) throw new AppException("Token not found.");
            if (!"WAITING".equals(t.getStatus())) throw new AppException("Only a waiting token can be cancelled.");
            tokenDao.updateStatus(c, tokenId, "CANCELLED", null);
            historyDao.insert(c, tokenId, null, "CANCELLED", "Cancelled by student");
            return null;
        });
    }

    public List<Token> activeTokens(int studentId) {
        try (Connection c = DBConnection.getConnection()) {
            List<Token> list = tokenDao.findByStudent(c, studentId, true);
            for (Token t : list) if ("WAITING".equals(t.getStatus())) t.setPosition(tokenDao.position(c, t.getServiceId(), t.getId()));
            return list;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<Token> allTokens(int studentId) {
        try (Connection c = DBConnection.getConnection()) { return tokenDao.findByStudent(c, studentId, false); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<Notification> notifications(int studentId, boolean markRead) {
        try (Connection c = DBConnection.getConnection()) {
            List<Notification> list = notificationDao.findByStudent(c, studentId);
            if (markRead) notificationDao.markAllRead(c, studentId);
            return list;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public int unreadCount(int studentId) {
        try (Connection c = DBConnection.getConnection()) { return notificationDao.unreadCount(c, studentId); }
        catch (SQLException e) { return 0; }
    }

    // ---------------- staff actions ----------------

    /** Calls the next waiting token of the staff member's service. Returns null when nobody is waiting. */
    public Token callNext(int staffId, int serviceId) {
        synchronized (lockFor(serviceId)) {
            return inTransaction(c -> {
                serviceDao.findByIdForUpdate(c, serviceId);
                if (tokenDao.findCalled(c, serviceId) != null)
                    throw new AppException("Finish the current token (complete or skip) before calling the next one.");
                Token next = tokenDao.findNextWaitingForUpdate(c, serviceId);
                if (next == null) return null;
                tokenDao.updateStatus(c, next.getId(), "CALLED", staffId);
                historyDao.insert(c, next.getId(), staffId, "CALLED", "Token called");
                notifyStudent(c, next.getStudentId(), next.getId(), "Your token " + next.getNumber() + " is being called. Please go to the counter now.");
                List<Token> waiting = tokenDao.findWaiting(c, serviceId);
                if (!waiting.isEmpty())
                    notifyStudent(c, waiting.get(0).getStudentId(), waiting.get(0).getId(), "You are next in line for token " + waiting.get(0).getNumber() + ". Please be ready.");
                return tokenDao.findById(c, next.getId());
            });
        }
    }

    public void finish(int staffId, int serviceId, int tokenId, boolean complete) {
        synchronized (lockFor(serviceId)) {
            inTransaction(c -> {
                Token t = tokenDao.findByIdForUpdate(c, tokenId);
                if (t == null || t.getServiceId() != serviceId) throw new AppException("Token not found for your service.");
                if (!"CALLED".equals(t.getStatus())) throw new AppException("Only a called token can be completed or skipped.");
                String status = complete ? "COMPLETED" : "SKIPPED";
                tokenDao.updateStatus(c, tokenId, status, staffId);
                historyDao.insert(c, tokenId, staffId, status, complete ? "Service completed" : "Token skipped (student absent)");
                notifyStudent(c, t.getStudentId(), tokenId, complete
                        ? "Your token " + t.getNumber() + " is completed. Thank you."
                        : "Your token " + t.getNumber() + " was skipped. Please request a new token if you still need the service.");
                return null;
            });
        }
    }

    public List<Token> waitingList(int serviceId) {
        try (Connection c = DBConnection.getConnection()) {
            List<Token> list = tokenDao.findWaiting(c, serviceId);
            int i = 1; for (Token t : list) t.setPosition(i++);
            return list;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public Token currentToken(int serviceId) {
        try (Connection c = DBConnection.getConnection()) { return tokenDao.findCalled(c, serviceId); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public List<HistoryRow> serviceHistory(int serviceId) {
        try (Connection c = DBConnection.getConnection()) { return historyDao.findByService(c, serviceId, 100); }
        catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    // ---------------- read-only data for REST / TCP ----------------

    public QueueStatus queueStatus(int serviceId) {
        try (Connection c = DBConnection.getConnection()) {
            Service sv = serviceDao.findById(c, serviceId);
            if (sv == null) return null;
            Token called = tokenDao.findCalled(c, serviceId);
            return new QueueStatus(serviceId, sv.getName(), tokenDao.waitingCount(c, serviceId), called == null ? null : called.getNumber());
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }

    public Token trackByNumber(String number) {
        try (Connection c = DBConnection.getConnection()) {
            Token t = tokenDao.findByNumber(c, number);
            if (t != null && "WAITING".equals(t.getStatus())) t.setPosition(tokenDao.position(c, t.getServiceId(), t.getId()));
            return t;
        } catch (SQLException e) { throw new AppException("Database error.", e); }
    }
}
