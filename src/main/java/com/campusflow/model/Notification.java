package com.campusflow.model;

import java.sql.Timestamp;

public class Notification {
    private final int id; private final String message; private final boolean read; private final Timestamp createdAt;

    public Notification(int id, String message, boolean read, Timestamp createdAt) {
        this.id = id; this.message = message; this.read = read; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public Timestamp getCreatedAt() { return createdAt; }
}
