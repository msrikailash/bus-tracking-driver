package com.example.bus_traker_manager.data.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.StopEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class StopDaoTest {
    private AppDatabase db;
    private StopDao dao;

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class).build();
        dao = db.stopDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetAll() {
        StopEntity entity = new StopEntity();
        entity.stopId = "stop1";
        entity.name = "Main St";
        dao.insert(entity);
        List<StopEntity> all = dao.getAll();
        assertEquals(1, all.size());
        assertEquals("stop1", all.get(0).stopId);
    }

    @Test
    public void deleteAll() {
        StopEntity entity = new StopEntity();
        entity.stopId = "stop2";
        dao.insert(entity);
        dao.deleteAll();
        List<StopEntity> all = dao.getAll();
        assertTrue(all.isEmpty());
    }
}

