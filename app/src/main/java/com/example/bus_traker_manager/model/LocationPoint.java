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
}

