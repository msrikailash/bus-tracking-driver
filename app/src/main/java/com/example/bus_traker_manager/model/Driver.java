package com.example.bus_traker_manager.model;

import java.io.Serializable;

public class Driver implements Serializable {
    private String driverId;
    private String name;
    private String busNo;
    private double lat;
    private double lng;
    private String status;
    private String email;
    private String phone;

    // Default constructor for Firebase
    public Driver() {
    }

    public Driver(String driverId, String name, String busNo, String email, String phone) {
        this.driverId = driverId;
        this.name = name;
        this.busNo = busNo;
        this.email = email;
        this.phone = phone;
        this.status = "offline";
        this.lat = 0.0;
        this.lng = 0.0;
    }

    // Getters and Setters
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

