package com.campusflow.model;

import java.sql.Timestamp;

public class Student {
    private final int id; private final String rollNo, fullName, email, phone, department, status, passwordHash;
    private final Timestamp createdAt;

    public Student(int id, String rollNo, String fullName, String email, String phone, String department,
                   String status, String passwordHash, Timestamp createdAt) {
        this.id = id; this.rollNo = rollNo; this.fullName = fullName; this.email = email; this.phone = phone;
        this.department = department; this.status = status; this.passwordHash = passwordHash; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public String getRollNo() { return rollNo; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public String getPasswordHash() { return passwordHash; }
    public Timestamp getCreatedAt() { return createdAt; }
}
