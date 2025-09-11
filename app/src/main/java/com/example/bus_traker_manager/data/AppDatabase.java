package com.example.bus_traker_manager.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.bus_traker_manager.data.dao.LocationDao;
import com.example.bus_traker_manager.data.dao.IssueDao;
import com.example.bus_traker_manager.data.dao.StopDao;
import com.example.bus_traker_manager.data.entity.LocationEntity;
import com.example.bus_traker_manager.data.entity.IssueEntity;
import com.example.bus_traker_manager.data.entity.StopEntity;

@Database(entities = {LocationEntity.class, IssueEntity.class, StopEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
    public abstract IssueDao issueDao();
    public abstract StopDao stopDao();
}
