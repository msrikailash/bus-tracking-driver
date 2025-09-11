package com.example.bus_traker_manager.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class LocationEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String tripId;
    public double lat;
    public double lng;
    public float speed;
    public float bearing;
    public float accuracy;
    public long timestamp;
    public int batteryPct;
}

