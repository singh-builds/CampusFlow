package com.campusflow.model;

import java.sql.Timestamp;

public class Staff {
    private final int id; private final String fullName, email, passwordHash, role, serviceName, status;
    private final Integer serviceId; private final Timestamp createdAt;

    public Staff(int id, String fullName, String email, String passwordHash, String role, Integer serviceId,
                 String serviceName, String status, Timestamp createdAt) {
        this.id = id; this.fullName = fullName; this.email = email; this.passwordHash = passwordHash; this.role = role;
        this.serviceId = serviceId; this.serviceName = serviceName; this.status = status; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Integer getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
