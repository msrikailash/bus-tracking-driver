package com.example.bus_traker_manager.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Trip implements Serializable {
    private String tripId;
    private String routeId;
    private String busId;
    private String driverId;
    private String tripType; // morning or afternoon
    private String scheduledDate;
    private String scheduledStartTime;
    private String scheduledEndTime;
    private long actualStartTime;
    private long actualEndTime;
    private String status; // scheduled, in_progress, completed, cancelled
    private int currentStopIndex;
    private List<StopProgress> stopsProgress;
    private AttendanceSummary attendance;
    private RouteInfo route;
    private List<TripIssue> issues;
    private long createdAt;
    private long updatedAt;
    
    // Default constructor for Firebase
    public Trip() {}
    
    public Trip(String tripId, String routeId, String busId, String driverId, String tripType, String scheduledDate) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.busId = busId;
        this.driverId = driverId;
        this.tripType = tripType;
        this.scheduledDate = scheduledDate;
        this.status = "scheduled";
        this.currentStopIndex = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    // Inner class for stop progress
    public static class StopProgress implements Serializable {
        private String stopId;
        private String stopName;
        private String scheduledTime;
        private long actualTime;
        private String status; // pending, reached, departed
        private int studentsPresent;
        private int studentsAbsent;
        
        public StopProgress() {}
        
        public StopProgress(String stopId, String stopName, String scheduledTime) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.scheduledTime = scheduledTime;
            this.status = "pending";
            this.studentsPresent = 0;
            this.studentsAbsent = 0;
        }
        
        // Getters and Setters
        public String getStopId() { return stopId; }
        public void setStopId(String stopId) { this.stopId = stopId; }
        
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        
        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
        
        public long getActualTime() { return actualTime; }
        public void setActualTime(long actualTime) { this.actualTime = actualTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getStudentsPresent() { return studentsPresent; }
        public void setStudentsPresent(int studentsPresent) { this.studentsPresent = studentsPresent; }
        
        public int getStudentsAbsent() { return studentsAbsent; }
        public void setStudentsAbsent(int studentsAbsent) { this.studentsAbsent = studentsAbsent; }
        
        // Helper methods
        public boolean isPending() { return "pending".equals(status); }
        public boolean isReached() { return "reached".equals(status); }
        public boolean isDeparted() { return "departed".equals(status); }
        public int getTotalStudents() { return studentsPresent + studentsAbsent; }
    }
    
    // Inner class for attendance summary
    public static class AttendanceSummary implements Serializable {
        private int totalStudents;
        private int presentCount;
        private int absentCount;
        private List<StudentAttendance> studentDetails;
        
        public AttendanceSummary() {}
        
        public AttendanceSummary(int totalStudents) {
            this.totalStudents = totalStudents;
            this.presentCount = 0;
            this.absentCount = 0;
        }
        
        // Getters and Setters
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public List<StudentAttendance> getStudentDetails() { return studentDetails; }
        public void setStudentDetails(List<StudentAttendance> studentDetails) { this.studentDetails = studentDetails; }
        
        // Helper methods
        public double getAttendancePercentage() {
            if (totalStudents == 0) return 0;
            return (double) presentCount / totalStudents * 100;
        }
    }
    
    // Inner class for student attendance details
    public static class StudentAttendance implements Serializable {
        private String studentId;
        private String studentName;
        private String status; // present, absent, late
        private long checkInTime;
        private LocationInfo checkInLocation;
        
        public StudentAttendance() {}
        
        public StudentAttendance(String studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.status = "absent";
        }
        
        // Getters and Setters
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public long getCheckInTime() { return checkInTime; }
        public void setCheckInTime(long checkInTime) { this.checkInTime = checkInTime; }
        
        public LocationInfo getCheckInLocation() { return checkInLocation; }
        public void setCheckInLocation(LocationInfo checkInLocation) { this.checkInLocation = checkInLocation; }
        
        // Helper methods
        public boolean isPresent() { return "present".equals(status); }
        public boolean isAbsent() { return "absent".equals(status); }
        public boolean isLate() { return "late".equals(status); }
    }
    
    // Inner class for location information
    public static class LocationInfo implements Serializable {
        private double latitude;
        private double longitude;
        
        public LocationInfo() {}
        
        public LocationInfo(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
    
    // Inner class for route information
    public static class RouteInfo implements Serializable {
        private double totalDistance;
        private double actualDistance;
        private int estimatedDuration;
        private long actualDuration;
        
        public RouteInfo() {}
        
        public RouteInfo(double totalDistance, int estimatedDuration) {
            this.totalDistance = totalDistance;
            this.estimatedDuration = estimatedDuration;
            this.actualDistance = 0;
            this.actualDuration = 0;
        }
        
        // Getters and Setters
        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
        
        public double getActualDistance() { return actualDistance; }
        public void setActualDistance(double actualDistance) { this.actualDistance = actualDistance; }
        
        public int getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        
        public long getActualDuration() { return actualDuration; }
        public void setActualDuration(long actualDuration) { this.actualDuration = actualDuration; }
    }
    
    // Inner class for trip issues
    public static class TripIssue implements Serializable {
        private String type; // delay, breakdown, emergency, other
        private String description;
        private long timestamp;
        private LocationInfo location;
        private boolean resolved;
        private long resolutionTime;
        
        public TripIssue() {}
        
        public TripIssue(String type, String description) {
            this.type = type;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
            this.resolved = false;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public LocationInfo getLocation() { return location; }
        public void setLocation(LocationInfo location) { this.location = location; }
        
        public boolean isResolved() { return resolved; }
        public void setResolved(boolean resolved) { this.resolved = resolved; }
        
        public long getResolutionTime() { return resolutionTime; }
        public void setResolutionTime(long resolutionTime) { this.resolutionTime = resolutionTime; }
    }
    
    // Main class getters and setters
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    
    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }
    
    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public String getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(String scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }
    
    public String getScheduledEndTime() { return scheduledEndTime; }
    public void setScheduledEndTime(String scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; }
    
    public long getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(long actualStartTime) { this.actualStartTime = actualStartTime; }
    
    public long getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(long actualEndTime) { this.actualEndTime = actualEndTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getCurrentStopIndex() { return currentStopIndex; }
    public void setCurrentStopIndex(int currentStopIndex) { this.currentStopIndex = currentStopIndex; }
    
    public List<StopProgress> getStopsProgress() { return stopsProgress; }
    public void setStopsProgress(List<StopProgress> stopsProgress) { this.stopsProgress = stopsProgress; }
    
    public AttendanceSummary getAttendance() { return attendance; }
    public void setAttendance(AttendanceSummary attendance) { this.attendance = attendance; }
    
    public RouteInfo getRoute() { return route; }
    public void setRoute(RouteInfo route) { this.route = route; }
    
    public List<TripIssue> getIssues() { return issues; }
    public void setIssues(List<TripIssue> issues) { this.issues = issues; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isScheduled() { return "scheduled".equals(status); }
    public boolean isInProgress() { return "in_progress".equals(status); }
    public boolean isCompleted() { return "completed".equals(status); }
    public boolean isCancelled() { return "cancelled".equals(status); }
    
    public boolean isMorningTrip() { return "morning".equals(tripType); }
    public boolean isAfternoonTrip() { return "afternoon".equals(tripType); }
    
    public long getDuration() {
        if (actualStartTime == 0) return 0;
        long endTime = actualEndTime > 0 ? actualEndTime : System.currentTimeMillis();
        return endTime - actualStartTime;
    }
    
    public String getFormattedDuration() {
        long duration = getDuration();
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%dh %dm", hours, minutes);
    }
    
    public String getStatusDisplay() {
        switch (status) {
            case "scheduled": return "Scheduled";
            case "in_progress": return "In Progress";
            case "completed": return "Completed";
            case "cancelled": return "Cancelled";
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
    
    public StopProgress getCurrentStop() {
        if (stopsProgress != null && currentStopIndex >= 0 && currentStopIndex < stopsProgress.size()) {
            return stopsProgress.get(currentStopIndex);
        }
        return null;
    }
    
    public int getCompletedStops() {
        if (stopsProgress == null) return 0;
        int completed = 0;
        for (StopProgress stop : stopsProgress) {
            if (stop.isReached() || stop.isDeparted()) {
                completed++;
            }
        }
        return completed;
    }
    
    public double getProgressPercentage() {
        if (stopsProgress == null || stopsProgress.isEmpty()) return 0;
        return (double) getCompletedStops() / stopsProgress.size() * 100;
    }
}

