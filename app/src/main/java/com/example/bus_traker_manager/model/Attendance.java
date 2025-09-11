package com.example.bus_traker_manager.model;

import java.io.Serializable;

public class Attendance implements Serializable {
    private String attendanceId;
    private String tripId;
    private String studentId;
    private String date;
    private String tripType; // morning or afternoon
    private String status; // present, absent, late
    private long checkInTime;
    private long checkOutTime;
    private LocationInfo checkInLocation;
    private LocationInfo checkOutLocation;
    private String verificationMethod; // qr_scan, manual, gps
    private String notes;
    private String createdBy; // driverId
    private long createdAt;
    private long updatedAt;

    // Default constructor for Firebase
    public Attendance() {
    }

    public Attendance(String attendanceId, String tripId, String studentId, String date, String tripType) {
        this.attendanceId = attendanceId;
        this.tripId = tripId;
        this.studentId = studentId;
        this.date = date;
        this.tripType = tripType;
        this.status = "absent"; // default status
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Inner class for location information
    public static class LocationInfo implements Serializable {
        private double latitude;
        private double longitude;
        private String stopId;

        public LocationInfo() {}

        public LocationInfo(double latitude, double longitude, String stopId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.stopId = stopId;
        }

        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public String getStopId() { return stopId; }
        public void setStopId(String stopId) { this.stopId = stopId; }
    }

    // Main class getters and setters
    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCheckInTime() { return checkInTime; }
    public void setCheckInTime(long checkInTime) { this.checkInTime = checkInTime; }

    public long getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(long checkOutTime) { this.checkOutTime = checkOutTime; }

    public LocationInfo getCheckInLocation() { return checkInLocation; }
    public void setCheckInLocation(LocationInfo checkInLocation) { this.checkInLocation = checkInLocation; }

    public LocationInfo getCheckOutLocation() { return checkOutLocation; }
    public void setCheckOutLocation(LocationInfo checkOutLocation) { this.checkOutLocation = checkOutLocation; }

    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isPresent() {
        return "present".equals(status);
    }

    public boolean isAbsent() {
        return "absent".equals(status);
    }

    public boolean isLate() {
        return "late".equals(status);
    }

    public boolean isMorningTrip() {
        return "morning".equals(tripType);
    }

    public boolean isAfternoonTrip() {
        return "afternoon".equals(tripType);
    }

    public boolean isCheckedIn() {
        return checkInTime > 0;
    }

    public boolean isCheckedOut() {
        return checkOutTime > 0;
    }

    public boolean isQrScanned() {
        return "qr_scan".equals(verificationMethod);
    }

    public boolean isManualEntry() {
        return "manual".equals(verificationMethod);
    }

    public boolean isGpsVerified() {
        return "gps".equals(verificationMethod);
    }

    public String getStatusDisplay() {
        switch (status) {
            case "present": return "Present";
            case "absent": return "Absent";
            case "late": return "Late";
            default: return "Unknown";
        }
    }

    public String getTripTypeDisplay() {
        switch (tripType) {
            case "morning": return "Morning";
            case "afternoon": return "Afternoon";
            default: return "Unknown";
        }
    }

    public String getVerificationMethodDisplay() {
        switch (verificationMethod) {
            case "qr_scan": return "QR Scan";
            case "manual": return "Manual";
            case "gps": return "GPS";
            default: return "Unknown";
        }
    }
}
