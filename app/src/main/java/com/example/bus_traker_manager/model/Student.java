package com.example.bus_traker_manager.model;

import java.io.Serializable;

public class Student implements Serializable {
    private String studentId;
    private String name;
    private String rollNumber;
    private String grade;
    private String section;
    private String photoUrl;
    private ContactInfo contactInfo;
    private ParentInfo parentInfo;
    private BusAssignment busAssignment;
    private Stops stops;
    private String status;
    private String qrCode;
    private long createdAt;
    private long updatedAt;

    // Default constructor for Firebase
    public Student() {
    }

    public Student(String studentId, String name, String rollNumber, String grade, String section) {
        this.studentId = studentId;
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
        this.section = section;
        this.status = "active";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Inner classes for structured data
    public static class ContactInfo implements Serializable {
        private String phone;
        private String email;
        private String address;

        public ContactInfo() {}

        public ContactInfo(String phone, String email, String address) {
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        // Getters and Setters
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class ParentInfo implements Serializable {
        private String fatherName;
        private String motherName;
        private String fatherPhone;
        private String motherPhone;
        private String emergencyContact;

        public ParentInfo() {}

        public ParentInfo(String fatherName, String motherName, String fatherPhone, String motherPhone, String emergencyContact) {
            this.fatherName = fatherName;
            this.motherName = motherName;
            this.fatherPhone = fatherPhone;
            this.motherPhone = motherPhone;
            this.emergencyContact = emergencyContact;
        }

        // Getters and Setters
        public String getFatherName() { return fatherName; }
        public void setFatherName(String fatherName) { this.fatherName = fatherName; }
        public String getMotherName() { return motherName; }
        public void setMotherName(String motherName) { this.motherName = motherName; }
        public String getFatherPhone() { return fatherPhone; }
        public void setFatherPhone(String fatherPhone) { this.fatherPhone = fatherPhone; }
        public String getMotherPhone() { return motherPhone; }
        public void setMotherPhone(String motherPhone) { this.motherPhone = motherPhone; }
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    }

    public static class BusAssignment implements Serializable {
        private String busId;
        private String busNumber;
        private String routeId;

        public BusAssignment() {}

        public BusAssignment(String busId, String busNumber, String routeId) {
            this.busId = busId;
            this.busNumber = busNumber;
            this.routeId = routeId;
        }

        // Getters and Setters
        public String getBusId() { return busId; }
        public void setBusId(String busId) { this.busId = busId; }
        public String getBusNumber() { return busNumber; }
        public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
    }

    public static class Stops implements Serializable {
        private StopInfo pickupStop;
        private StopInfo dropoffStop;

        public Stops() {}

        public Stops(StopInfo pickupStop, StopInfo dropoffStop) {
            this.pickupStop = pickupStop;
            this.dropoffStop = dropoffStop;
        }

        // Getters and Setters
        public StopInfo getPickupStop() { return pickupStop; }
        public void setPickupStop(StopInfo pickupStop) { this.pickupStop = pickupStop; }
        public StopInfo getDropoffStop() { return dropoffStop; }
        public void setDropoffStop(StopInfo dropoffStop) { this.dropoffStop = dropoffStop; }
    }

    public static class StopInfo implements Serializable {
        private String stopId;
        private String stopName;
        private String estimatedTime;

        public StopInfo() {}

        public StopInfo(String stopId, String stopName, String estimatedTime) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.estimatedTime = estimatedTime;
        }

        // Getters and Setters
        public String getStopId() { return stopId; }
        public void setStopId(String stopId) { this.stopId = stopId; }
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    }

    // Main class getters and setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }

    public ParentInfo getParentInfo() { return parentInfo; }
    public void setParentInfo(ParentInfo parentInfo) { this.parentInfo = parentInfo; }

    public BusAssignment getBusAssignment() { return busAssignment; }
    public void setBusAssignment(BusAssignment busAssignment) { this.busAssignment = busAssignment; }

    public Stops getStops() { return stops; }
    public void setStops(Stops stops) { this.stops = stops; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getFullName() {
        return name;
    }

    public String getGradeSection() {
        return grade + " - " + section;
    }

    public boolean isActive() {
        return "active".equals(status);
    }

    public String getDisplayName() {
        return name + " (" + rollNumber + ")";
    }
}