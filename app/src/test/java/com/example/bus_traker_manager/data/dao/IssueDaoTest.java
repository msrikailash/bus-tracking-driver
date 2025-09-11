package com.example.bus_traker_manager.data.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.IssueEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class IssueDaoTest {
    private AppDatabase db;
    private IssueDao dao;

    @Before
    public void createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class).build();
        dao = db.issueDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetAll() {
        IssueEntity entity = new IssueEntity();
        entity.uid = "user1";
        entity.text = "Test issue";
        dao.insert(entity);
        List<IssueEntity> all = dao.getAll();
        assertEquals(1, all.size());
        assertEquals("user1", all.get(0).uid);
    }

    @Test
    public void deleteAll() {
        IssueEntity entity = new IssueEntity();
        entity.uid = "user2";
        dao.insert(entity);
        dao.deleteAll();
        List<IssueEntity> all = dao.getAll();
        assertTrue(all.isEmpty());
    }
}

