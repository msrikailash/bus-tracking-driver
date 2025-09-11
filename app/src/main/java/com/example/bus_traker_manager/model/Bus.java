package com.example.bus_traker_manager.model;

public class Bus {
    public String busId;
    public String plate;
    public String routeId;
    public String status;

    public Bus() {}

    public Bus(String busId, String plate, String routeId, String status) {
        this.busId = busId;
        this.plate = plate;
        this.routeId = routeId;
        this.status = status;
    }
}

