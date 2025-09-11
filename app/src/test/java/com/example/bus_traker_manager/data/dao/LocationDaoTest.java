package com.example.bus_traker_manager.data.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.LocationEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocationDaoTest {
    private AppDatabase db;
    private LocationDao dao;

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class).build();
        dao = db.locationDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetAll() {
        LocationEntity entity = new LocationEntity();
        entity.tripId = "trip1";
        entity.lat = 1.0;
        entity.lng = 2.0;
        dao.insert(entity);
        List<LocationEntity> all = dao.getAll();
        assertEquals(1, all.size());
        assertEquals("trip1", all.get(0).tripId);
    }

    @Test
    public void deleteAll() {
        LocationEntity entity = new LocationEntity();
        entity.tripId = "trip2";
        dao.insert(entity);
        dao.deleteAll();
        List<LocationEntity> all = dao.getAll();
        assertTrue(all.isEmpty());
    }
}

