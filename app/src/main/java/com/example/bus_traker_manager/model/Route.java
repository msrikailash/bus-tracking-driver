package com.example.bus_traker_manager.model;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable {
    private String routeId;
    private String routeName;
    private String routeCode;
    private String description;
    private String assignedBus;
    private String assignedDriver;
    private List<RouteStop> stops;
    private double totalDistance; // in kilometers
    private int estimatedDuration; // in minutes
    private String status; // active or inactive
    private Schedule schedule;
    private long createdAt;
    private long updatedAt;

    // Default constructor for Firebase
    public Route() {
    }

    public Route(String routeId, String routeName, String routeCode, String description) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.description = description;
        this.status = "active";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Inner class for route stops
    public static class RouteStop implements Serializable {
        private String stopId;
        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private int order;
        private String estimatedTime; // HH:MM format
        private String stopType; // pickup, dropoff, or both

        public RouteStop() {}

        public RouteStop(String stopId, String name, String address, double latitude, double longitude, int order, String estimatedTime, String stopType) {
            this.stopId = stopId;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.order = order;
            this.estimatedTime = estimatedTime;
            this.stopType = stopType;
        }

        // Getters and Setters
        public String getStopId() { return stopId; }
        public void setStopId(String stopId) { this.stopId = stopId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }

        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

        public String getStopType() { return stopType; }
        public void setStopType(String stopType) { this.stopType = stopType; }

        // Helper methods
        public boolean isPickupStop() {
            return "pickup".equals(stopType) || "both".equals(stopType);
        }

        public boolean isDropoffStop() {
            return "dropoff".equals(stopType) || "both".equals(stopType);
        }

        public String getStopTypeDisplay() {
            switch (stopType) {
                case "pickup": return "Pickup Only";
                case "dropoff": return "Dropoff Only";
                case "both": return "Pickup & Dropoff";
                default: return "Unknown";
            }
        }
    }

    // Inner class for schedule
    public static class Schedule implements Serializable {
        private TimeSlot morning;
        private TimeSlot afternoon;

        public Schedule() {}

        public Schedule(TimeSlot morning, TimeSlot afternoon) {
            this.morning = morning;
            this.afternoon = afternoon;
        }

        // Getters and Setters
        public TimeSlot getMorning() { return morning; }
        public void setMorning(TimeSlot morning) { this.morning = morning; }

        public TimeSlot getAfternoon() { return afternoon; }
        public void setAfternoon(TimeSlot afternoon) { this.afternoon = afternoon; }
    }

    // Inner class for time slots
    public static class TimeSlot implements Serializable {
        private String startTime; // HH:MM format
        private String endTime; // HH:MM format

        public TimeSlot() {}

        public TimeSlot(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        // Getters and Setters
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }

    // Main class getters and setters
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssignedBus() { return assignedBus; }
    public void setAssignedBus(String assignedBus) { this.assignedBus = assignedBus; }

    public String getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(String assignedDriver) { this.assignedDriver = assignedDriver; }

    public List<RouteStop> getStops() { return stops; }
    public void setStops(List<RouteStop> stops) { this.stops = stops; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public int getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isInactive() {
        return "inactive".equals(status);
    }

    public int getStopCount() {
        return stops != null ? stops.size() : 0;
    }

    public String getDurationDisplay() {
        if (estimatedDuration < 60) {
            return estimatedDuration + " min";
        } else {
            int hours = estimatedDuration / 60;
            int minutes = estimatedDuration % 60;
            if (minutes == 0) {
                return hours + " hr";
            } else {
                return hours + " hr " + minutes + " min";
            }
        }
    }

    public String getDistanceDisplay() {
        if (totalDistance < 1) {
            return String.format("%.0f m", totalDistance * 1000);
        } else {
            return String.format("%.1f km", totalDistance);
        }
    }

    public RouteStop getStopByOrder(int order) {
        if (stops != null) {
            for (RouteStop stop : stops) {
                if (stop.getOrder() == order) {
                    return stop;
                }
            }
        }
        return null;
    }

    public RouteStop getFirstStop() {
        return getStopByOrder(1);
    }

    public RouteStop getLastStop() {
        if (stops != null && !stops.isEmpty()) {
            int maxOrder = 0;
            RouteStop lastStop = null;
            for (RouteStop stop : stops) {
                if (stop.getOrder() > maxOrder) {
                    maxOrder = stop.getOrder();
                    lastStop = stop;
                }
            }
            return lastStop;
        }
        return null;
    }

    public String getStatusDisplay() {
        return isActive() ? "Active" : "Inactive";
    }
} 