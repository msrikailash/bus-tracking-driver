package com.example.bus_traker_manager.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stops")
public class StopEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String stopId;
    public String name;
    public double lat;
    public double lng;
    public float radiusM;
}

