package com.example.bus_traker_manager.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.bus_traker_manager.data.entity.IssueEntity;
import java.util.List;

@Dao
public interface IssueDao {
    @Insert
    void insert(IssueEntity issue);

    @Query("SELECT * FROM issues")
    List<IssueEntity> getAll();

    @Query("DELETE FROM issues")
    void deleteAll();
}

