package com.example.bus_traker_manager.model;

import java.util.Date;

public class Leave {
    private String leaveId;
    private String driverId;
    private String reason;
    private String status;
    private Date requestDate;
    private Date leaveDate;
    private String driverName;
    private String busNo;

    // Default constructor for Firebase
    public Leave() {
    }

    public Leave(String leaveId, String driverId, String reason, Date leaveDate, String driverName, String busNo) {
        this.leaveId = leaveId;
        this.driverId = driverId;
        this.reason = reason;
        this.leaveDate = leaveDate;
        this.driverName = driverName;
        this.busNo = busNo;
        this.status = "pending";
        this.requestDate = new Date();
    }

    // Getters and Setters
    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }
}
