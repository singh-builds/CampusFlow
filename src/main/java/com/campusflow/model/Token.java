package com.campusflow.model;

import java.sql.Timestamp;

public class Token {
    private final int id, studentId, serviceId; private final String number, studentName, serviceName, status;
    private final Timestamp createdAt, calledAt, completedAt; private int position; // position = place in the waiting line

    public Token(int id, String number, int studentId, String studentName, int serviceId, String serviceName,
                 String status, Timestamp createdAt, Timestamp calledAt, Timestamp completedAt) {
        this.id = id; this.number = number; this.studentId = studentId; this.studentName = studentName;
        this.serviceId = serviceId; this.serviceName = serviceName; this.status = status;
        this.createdAt = createdAt; this.calledAt = calledAt; this.completedAt = completedAt;
    }
    public int getId() { return id; }
    public String getNumber() { return number; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public int getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getCalledAt() { return calledAt; }
    public Timestamp getCompletedAt() { return completedAt; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
