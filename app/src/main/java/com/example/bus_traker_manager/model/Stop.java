package com.example.bus_traker_manager.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Stop implements Serializable {
    // Basic Information
    private String id;
    private String name;
    private String busNo;
    private String stopCode;
    private String description;
    private String stopType; // pickup_only, drop_only, pickup_drop
    private String landmark;
    private String createdBy;
    private long createdAt;
    private long updatedAt;

    // Location Information
    private LocationInfo location;

    // Schedule Information
    private ScheduleInfo schedule;

    // Status Information
    private StatusInfo status;

    // Tracking Information
    private TrackingInfo tracking;

    // Metadata Information
    private MetadataInfo metadata;

    // Nested Classes
    public static class LocationInfo implements Serializable {
        private double latitude;
        private double longitude;
        private String address;
        private double accuracy;
        private double altitude;
        private int geofenceRadius;
        private String locationType; // junction, residential, commercial, etc.
        private String accessibility; // easy, moderate, difficult
        private boolean parkingAvailable;
        private boolean shelterAvailable;

        // Constructors
        public LocationInfo() {}

        public LocationInfo(double latitude, double longitude, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.accuracy = 5.0;
            this.geofenceRadius = 100;
            this.locationType = "junction";
            this.accessibility = "easy";
            this.parkingAvailable = true;
            this.shelterAvailable = true;
        }

        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

        public double getAltitude() { return altitude; }
        public void setAltitude(double altitude) { this.altitude = altitude; }

        public int getGeofenceRadius() { return geofenceRadius; }
        public void setGeofenceRadius(int geofenceRadius) { this.geofenceRadius = geofenceRadius; }

        public String getLocationType() { return locationType; }
        public void setLocationType(String locationType) { this.locationType = locationType; }

        public String getAccessibility() { return accessibility; }
        public void setAccessibility(String accessibility) { this.accessibility = accessibility; }

        public boolean isParkingAvailable() { return parkingAvailable; }
        public void setParkingAvailable(boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }

        public boolean isShelterAvailable() { return shelterAvailable; }
        public void setShelterAvailable(boolean shelterAvailable) { this.shelterAvailable = shelterAvailable; }
    }

    public static class ScheduleInfo implements Serializable {
        private String morningPickup;
        private String morningDrop;
        private String afternoonPickup;
        private String afternoonDrop;
        private String eveningPickup;
        private String eveningDrop;
        private Map<String, DaySchedule> weekendSchedule;
        private DaySchedule holidaySchedule;
        private int estimatedDuration; // in minutes
        private int bufferTime; // in minutes

        public static class DaySchedule implements Serializable {
            private String pickup;
            private String drop;

            public DaySchedule() {}

            public DaySchedule(String pickup, String drop) {
                this.pickup = pickup;
                this.drop = drop;
            }

            public String getPickup() { return pickup; }
            public void setPickup(String pickup) { this.pickup = pickup; }

            public String getDrop() { return drop; }
            public void setDrop(String drop) { this.drop = drop; }
        }

        // Constructors
        public ScheduleInfo() {
            this.estimatedDuration = 15;
            this.bufferTime = 5;
        }

        // Getters and Setters
        public String getMorningPickup() { return morningPickup; }
        public void setMorningPickup(String morningPickup) { this.morningPickup = morningPickup; }

        public String getMorningDrop() { return morningDrop; }
        public void setMorningDrop(String morningDrop) { this.morningDrop = morningDrop; }

        public String getAfternoonPickup() { return afternoonPickup; }
        public void setAfternoonPickup(String afternoonPickup) { this.afternoonPickup = afternoonPickup; }

        public String getAfternoonDrop() { return afternoonDrop; }
        public void setAfternoonDrop(String afternoonDrop) { this.afternoonDrop = afternoonDrop; }

        public String getEveningPickup() { return eveningPickup; }
        public void setEveningPickup(String eveningPickup) { this.eveningPickup = eveningPickup; }

        public String getEveningDrop() { return eveningDrop; }
        public void setEveningDrop(String eveningDrop) { this.eveningDrop = eveningDrop; }

        public Map<String, DaySchedule> getWeekendSchedule() { return weekendSchedule; }
        public void setWeekendSchedule(Map<String, DaySchedule> weekendSchedule) { this.weekendSchedule = weekendSchedule; }

        public DaySchedule getHolidaySchedule() { return holidaySchedule; }
        public void setHolidaySchedule(DaySchedule holidaySchedule) { this.holidaySchedule = holidaySchedule; }

        public int getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }

        public int getBufferTime() { return bufferTime; }
        public void setBufferTime(int bufferTime) { this.bufferTime = bufferTime; }
    }

    public static class StatusInfo implements Serializable {
        private boolean isActive;
        private boolean isReached;
        private boolean isSkipped;
        private String currentStatus; // pending, reached, skipped, maintenance
        private long lastStatusUpdate;
        private List<StatusHistory> statusHistory;
        private boolean maintenanceRequired;
        private String maintenanceNotes;
        private boolean isTemporary;
        private Long temporaryUntil;

        public static class StatusHistory implements Serializable {
            private String status;
            private long timestamp;
            private String driverId;
            private String notes;

            public StatusHistory() {}

            public StatusHistory(String status, long timestamp, String driverId, String notes) {
                this.status = status;
                this.timestamp = timestamp;
                this.driverId = driverId;
                this.notes = notes;
            }

            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }

            public long getTimestamp() { return timestamp; }
            public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

            public String getDriverId() { return driverId; }
            public void setDriverId(String driverId) { this.driverId = driverId; }

            public String getNotes() { return notes; }
            public void setNotes(String notes) { this.notes = notes; }
        }

        // Constructors
        public StatusInfo() {
            this.isActive = true;
            this.isReached = false;
            this.isSkipped = false;
            this.currentStatus = "pending";
            this.lastStatusUpdate = System.currentTimeMillis();
            this.maintenanceRequired = false;
            this.isTemporary = false;
        }

        // Getters and Setters
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        public void setIsActive(boolean active) { isActive = active; }

        public boolean isReached() { return isReached; }
        public void setReached(boolean reached) { isReached = reached; }

        public boolean isSkipped() { return isSkipped; }
        public void setSkipped(boolean skipped) { isSkipped = skipped; }

        public String getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

        public long getLastStatusUpdate() { return lastStatusUpdate; }
        public void setLastStatusUpdate(long lastStatusUpdate) { this.lastStatusUpdate = lastStatusUpdate; }

        public List<StatusHistory> getStatusHistory() { return statusHistory; }
        public void setStatusHistory(List<StatusHistory> statusHistory) { this.statusHistory = statusHistory; }

        public boolean isMaintenanceRequired() { return maintenanceRequired; }
        public void setMaintenanceRequired(boolean maintenanceRequired) { this.maintenanceRequired = maintenanceRequired; }

        public String getMaintenanceNotes() { return maintenanceNotes; }
        public void setMaintenanceNotes(String maintenanceNotes) { this.maintenanceNotes = maintenanceNotes; }

        public boolean isTemporary() { return isTemporary; }
        public void setTemporary(boolean temporary) { isTemporary = temporary; }

        public Long getTemporaryUntil() { return temporaryUntil; }
        public void setTemporaryUntil(Long temporaryUntil) { this.temporaryUntil = temporaryUntil; }
    }

    public static class TrackingInfo implements Serializable {
        private long estimatedArrivalTime;
        private Long actualArrivalTime;
        private Long actualDepartureTime;
        private int delayMinutes;
        private int passengersBoarding;
        private int passengersAlighting;
        private int currentPassengerCount;
        private int maxCapacity;
        private String weatherCondition; // clear, rain, fog, etc.
        private String trafficCondition; // normal, heavy, light
        private boolean routeDeviation;
        private String deviationReason;
        private List<LocationUpdate> locationUpdates;

        public static class LocationUpdate implements Serializable {
            private double latitude;
            private double longitude;
            private long timestamp;
            private double accuracy;
            private double speed;

            public LocationUpdate() {}

            public LocationUpdate(double latitude, double longitude, long timestamp, double accuracy, double speed) {
                this.latitude = latitude;
                this.longitude = longitude;
                this.timestamp = timestamp;
                this.accuracy = accuracy;
                this.speed = speed;
            }

            public double getLatitude() { return latitude; }
            public void setLatitude(double latitude) { this.latitude = latitude; }

            public double getLongitude() { return longitude; }
            public void setLongitude(double longitude) { this.longitude = longitude; }

            public long getTimestamp() { return timestamp; }
            public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

            public double getAccuracy() { return accuracy; }
            public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

            public double getSpeed() { return speed; }
            public void setSpeed(double speed) { this.speed = speed; }
        }

        // Constructors
        public TrackingInfo() {
            this.delayMinutes = 0;
            this.passengersBoarding = 0;
            this.passengersAlighting = 0;
            this.currentPassengerCount = 0;
            this.maxCapacity = 50;
            this.weatherCondition = "clear";
            this.trafficCondition = "normal";
            this.routeDeviation = false;
        }

        // Getters and Setters
        public long getEstimatedArrivalTime() { return estimatedArrivalTime; }
        public void setEstimatedArrivalTime(long estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }

        public Long getActualArrivalTime() { return actualArrivalTime; }
        public void setActualArrivalTime(Long actualArrivalTime) { this.actualArrivalTime = actualArrivalTime; }

        public Long getActualDepartureTime() { return actualDepartureTime; }
        public void setActualDepartureTime(Long actualDepartureTime) { this.actualDepartureTime = actualDepartureTime; }

        public int getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }

        public int getPassengersBoarding() { return passengersBoarding; }
        public void setPassengersBoarding(int passengersBoarding) { this.passengersBoarding = passengersBoarding; }

        public int getPassengersAlighting() { return passengersAlighting; }
        public void setPassengersAlighting(int passengersAlighting) { this.passengersAlighting = passengersAlighting; }

        public int getCurrentPassengerCount() { return currentPassengerCount; }
        public void setCurrentPassengerCount(int currentPassengerCount) { this.currentPassengerCount = currentPassengerCount; }

        public int getMaxCapacity() { return maxCapacity; }
        public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

        public String getWeatherCondition() { return weatherCondition; }
        public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

        public String getTrafficCondition() { return trafficCondition; }
        public void setTrafficCondition(String trafficCondition) { this.trafficCondition = trafficCondition; }

        public boolean isRouteDeviation() { return routeDeviation; }
        public void setRouteDeviation(boolean routeDeviation) { this.routeDeviation = routeDeviation; }

        public String getDeviationReason() { return deviationReason; }
        public void setDeviationReason(String deviationReason) { this.deviationReason = deviationReason; }

        public List<LocationUpdate> getLocationUpdates() { return locationUpdates; }
        public void setLocationUpdates(List<LocationUpdate> locationUpdates) { this.locationUpdates = locationUpdates; }
        
        public int getEstimatedDuration() { return 15; } // Default 15 minutes
        public void setEstimatedDuration(int estimatedDuration) { /* This would be calculated based on route */ }
    }

    public static class MetadataInfo implements Serializable {
        private String version;
        private long lastSync;
        private String syncStatus; // synced, pending, failed
        private boolean offlineData;
        private String dataSource;
        private String validationStatus; // validated, pending, failed
        private String validationNotes;
        private List<String> tags;
        private Map<String, String> customFields;

        // Constructors
        public MetadataInfo() {
            this.version = "1.0";
            this.lastSync = System.currentTimeMillis();
            this.syncStatus = "synced";
            this.offlineData = false;
            this.dataSource = "driver_app";
            this.validationStatus = "validated";
        }

        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public long getLastSync() { return lastSync; }
        public void setLastSync(long lastSync) { this.lastSync = lastSync; }

        public String getSyncStatus() { return syncStatus; }
        public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

        public boolean isOfflineData() { return offlineData; }
        public void setOfflineData(boolean offlineData) { this.offlineData = offlineData; }

        public String getDataSource() { return dataSource; }
        public void setDataSource(String dataSource) { this.dataSource = dataSource; }

        public String getValidationStatus() { return validationStatus; }
        public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

        public String getValidationNotes() { return validationNotes; }
        public void setValidationNotes(String validationNotes) { this.validationNotes = validationNotes; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }

        public Map<String, String> getCustomFields() { return customFields; }
        public void setCustomFields(Map<String, String> customFields) { this.customFields = customFields; }
    }

    // Main Stop Constructors
    public Stop() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.location = new LocationInfo();
        this.schedule = new ScheduleInfo();
        this.status = new StatusInfo();
        this.tracking = new TrackingInfo();
        this.metadata = new MetadataInfo();
    }

    public Stop(String id, String name, String busNo, double latitude, double longitude, String address) {
        this();
        this.id = id;
        this.name = name;
        this.busNo = busNo;
        this.location = new LocationInfo(latitude, longitude, address);
        this.stopType = "pickup_drop";
    }

    // Getters and Setters for main Stop class
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBusNo() { return busNo; }
    public void setBusNo(String busNo) { this.busNo = busNo; }

    public String getStopCode() { return stopCode; }
    public void setStopCode(String stopCode) { this.stopCode = stopCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStopType() { return stopType; }
    public void setStopType(String stopType) { this.stopType = stopType; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public LocationInfo getLocation() { return location; }
    public void setLocation(LocationInfo location) { this.location = location; }

    public ScheduleInfo getSchedule() { return schedule; }
    public void setSchedule(ScheduleInfo schedule) { this.schedule = schedule; }

    public StatusInfo getStatus() { return status; }
    public void setStatus(StatusInfo status) { this.status = status; }

    public TrackingInfo getTracking() { return tracking; }
    public void setTracking(TrackingInfo tracking) { this.tracking = tracking; }

    public MetadataInfo getMetadata() { return metadata; }
    public void setMetadata(MetadataInfo metadata) { this.metadata = metadata; }

    // Helper methods for backward compatibility
    public double getStopLat() { return location != null ? location.getLatitude() : 0.0; }
    public double getStopLng() { return location != null ? location.getLongitude() : 0.0; }
    public String getStopName() { return name; }
    public String getStopId() { return id; }
}

