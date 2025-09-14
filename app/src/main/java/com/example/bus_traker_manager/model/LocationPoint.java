package com.example.bus_traker_manager.model;

public class LocationPoint {
    public double lat;
    public double lng;
    public float speed;
    public float bearing;
    public float accuracy;
    public long timestamp;
    public int batteryPct;

    public LocationPoint() {}

    public LocationPoint(double lat, double lng, float speed, float bearing, float accuracy, long timestamp, int batteryPct) {
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.batteryPct = batteryPct;
    }

    // Constructor without batteryPct for compatibility
    public LocationPoint(double lat, double lng, float speed, float bearing, float accuracy, long timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.batteryPct = 0;
    }

    // Getter methods
    public double getLatitude() { return lat; }
    public double getLongitude() { return lng; }
    public float getSpeed() { return speed; }
    public float getBearing() { return bearing; }
    public float getAccuracy() { return accuracy; }
    public long getTimestamp() { return timestamp; }
    public int getBatteryPct() { return batteryPct; }

    // Setter methods
    public void setLatitude(double lat) { this.lat = lat; }
    public void setLongitude(double lng) { this.lng = lng; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setBearing(float bearing) { this.bearing = bearing; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setBatteryPct(int batteryPct) { this.batteryPct = batteryPct; }
}

