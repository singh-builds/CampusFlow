package com.campusflow.model;

import java.sql.Timestamp;

/** One line of the queue_history table (joined with token, service, student and staff names). */
public class HistoryRow {
    private final int id; private final String tokenNumber, serviceName, studentName, staffName, action, remarks;
    private final Timestamp actionTime;

    public HistoryRow(int id, String tokenNumber, String serviceName, String studentName, String staffName,
                      String action, Timestamp actionTime, String remarks) {
        this.id = id; this.tokenNumber = tokenNumber; this.serviceName = serviceName; this.studentName = studentName;
        this.staffName = staffName; this.action = action; this.actionTime = actionTime; this.remarks = remarks;
    }
    public int getId() { return id; }
    public String getTokenNumber() { return tokenNumber; }
    public String getServiceName() { return serviceName; }
    public String getStudentName() { return studentName; }
    public String getStaffName() { return staffName; }
    public String getAction() { return action; }
    public Timestamp getActionTime() { return actionTime; }
    public String getRemarks() { return remarks; }
}
