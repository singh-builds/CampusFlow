package com.campusflow.model;

import java.sql.Timestamp;

public class Service {
    private final int id; private final String name, prefix, description; private final boolean active;
    private final Timestamp createdAt; private int waiting; // waiting = filled in by the service layer for display

    public Service(int id, String name, String prefix, String description, boolean active, Timestamp createdAt) {
        this.id = id; this.name = name; this.prefix = prefix; this.description = description;
        this.active = active; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrefix() { return prefix; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public Timestamp getCreatedAt() { return createdAt; }
    public int getWaiting() { return waiting; }
    public void setWaiting(int waiting) { this.waiting = waiting; }
}
