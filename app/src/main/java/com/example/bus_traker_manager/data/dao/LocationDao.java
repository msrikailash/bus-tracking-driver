package com.example.bus_traker_manager.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bus_traker_manager.data.entity.LocationEntity;
import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(LocationEntity location);

    @Query("SELECT * FROM locations")
    List<LocationEntity> getAll();

    @Query("DELETE FROM locations")
    void deleteAll();
}

