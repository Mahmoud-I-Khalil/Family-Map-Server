package passoff;

import DataAccess.*;
import Model.EventMod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class EventTest {
    private Database db;
    private EventMod testEvent;
    private EventDao eDao;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        db = new Database();
        testEvent = new EventMod("hahaha", "randAlrabadi", "naiemSalim",
                122.34f,122.44f, "Jordan", "Ajlun", "Birthday", 1);
        Connection conn = db.getConnection();
        db.clearTables();
        eDao = new EventDao(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        eDao.insert(testEvent);
        EventMod compareTest = eDao.find(testEvent.getEventId());
        assertNotNull(compareTest);
        assertEquals(testEvent, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        eDao.insert(testEvent);
        assertThrows(DataAccessException.class, ()-> eDao.insert(testEvent));
    }

    @Test
    public void findPass() throws DataAccessException {
        eDao.insert(testEvent);
        EventMod compareTest = eDao.find(testEvent.getEventId());
        assertNotNull(compareTest);
        assertEquals(testEvent, compareTest);
    }


    @Test
    public void findFail() throws DataAccessException {
        assertNull(eDao.find("non existing EventID"));
    }


    @Test
    public void clearPass() throws DataAccessException {
        eDao.insert(testEvent);
        eDao.removeAll();
        //find a user with given username that doesn't exist
        assertNull(eDao.find("non existing eventID"));
    }
}