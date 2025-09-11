package com.example.bus_traker_manager.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bus_traker_manager.data.entity.StopEntity;
import java.util.List;

@Dao
public interface StopDao {
    @Insert
    void insert(StopEntity stop);

    @Query("SELECT * FROM stops")
    List<StopEntity> getAll();

    @Query("DELETE FROM stops")
    void deleteAll();
}

