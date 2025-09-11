package com.example.bus_traker_manager.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "issues")
public class IssueEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String uid;
    public String text;
    public String photoUrl;
    public long timestamp;
}

