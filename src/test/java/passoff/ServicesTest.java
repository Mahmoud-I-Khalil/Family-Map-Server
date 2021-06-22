package passoff;

import DataAccess.*;
import Model.AuthTokenMod;
import Model.EventMod;
import Model.PersonMod;
import Model.UserMod;
import Request.*;
import Result.*;
import Service.Clear;
import Service.*;
import Service.Person;
import Service.PersonId;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServicesTest {

    Database database = new Database();
    final String UNIQUE_ID = "332qa1";

    @BeforeEach
    public void SetUp() throws DataAccessException{
        database.openConnection();
        database.clearTables();
        Connection conn = database.getConnection();
        UserDao userDao = new UserDao(conn);
        EventDao eventDao = new EventDao(conn);
        PersonDao personDao = new PersonDao(conn);
        AuthTokenDoa authTokenDoa = new AuthTokenDoa(conn);

        UserMod user = new UserMod("MK","1234","email","firstName","LastName","m","abcd");
        EventMod eventMod = new EventMod(UNIQUE_ID,"MK", "LastName",4.0f,124.21f,"cows","caoew","birth",2000);
        PersonMod personMod = new PersonMod("g3aa","MK","person","LastName","m",null,null,null);
        AuthTokenMod authTokenMod = new AuthTokenMod("MK","2222");

        userDao.insert(user);
        eventDao.insert(eventMod);
        personDao.insert(personMod);
        authTokenDoa.insert(authTokenMod);

        database.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws Exception{
        database.openConnection();
        database.clearTables();
        database.closeConnection(true);
    }

    @Test
    public void clearPass() throws DataAccessException{
        Clear clear = new Clear();
        ClearResult clearResult = clear.clearService();
        assertTrue(clearResult.isSuccess());
    }

    @Test
    public void EventPass() throws DataAccessException{
        EventRequest eventRequest = new EventRequest("MK");
        Event event = new Event();
        EventResult eventResult = event.eventService(eventRequest);
        assertTrue(eventResult.isSuccess());
        assertFalse(eventResult.getData().isEmpty());
    }

    @Test
    public void EventFail() throws DataAccessException{
        EventRequest eventRequest = new EventRequest("M124K");
        Event event = new Event();
        EventResult eventResult = event.eventService(eventRequest);
        assertFalse(eventResult.isSuccess());

    }

    @Test
    public void EventIdPass() throws DataAccessException{
        EventIdRequest eventIdRequest = new EventIdRequest(UNIQUE_ID,"MK");
        EventID event = new EventID();
        EventIdResult eventIdResult = event.eventIdService(eventIdRequest);
        assertTrue(eventIdResult.isSuccess());
        assertEquals("birth", eventIdResult.getEventType());
    }

    @Test
    public void EventIdFail() throws DataAccessException{
        EventIdRequest eventIdRequest = new EventIdRequest("Non-Existant","MK");
        EventID event = new EventID();
        EventIdResult eventIdResult = event.eventIdService(eventIdRequest);
        assertFalse(eventIdResult.isSuccess());
    }

    @Test
    public void PersonPass() throws DataAccessException{
        PersonRequest personRequest = new PersonRequest("MK");
        Person person = new Person();
        PersonResult personResult = person.personService(personRequest);
        assertTrue(personResult.isSuccess());
        assertFalse(personResult.getData().isEmpty());
    }

    @Test
    public void PersonFail() throws DataAccessException{
        PersonRequest personRequest = new PersonRequest("NonExistant");
        Person person = new Person();
        PersonResult personResult = person.personService(personRequest);
        assertFalse(personResult.isSuccess());
    }

    @Test
    public void PersonIdPass() throws DataAccessException{
        PersonIdRequest personIdRequest = new PersonIdRequest("g3aa","MK");
        PersonId personId = new PersonId();
        PersonIdResult personIdResult = personId.personIdService(personIdRequest);

        assertTrue(personIdResult.isSuccess());
        assertEquals("MK",personIdResult.getAssociatedUsername());
    }

    @Test
    public void PersonIdFail() throws DataAccessException{
        PersonIdRequest personIdRequest = new PersonIdRequest("nonExistant","MK");
        PersonId personId = new PersonId();
        PersonIdResult personIdResult = personId.personIdService(personIdRequest);
        assertFalse(personIdResult.isSuccess());
    }

    @Test
    public void FillPass() throws DataAccessException {
        FillRequest fillRequest = new FillRequest("MK",4);
        Fill fill = new Fill();
        FillResult fillResult = fill.fillService(fillRequest);
        assertTrue(fillResult.isSuccess());
        database.openConnection();
        Connection conn = database.getConnection();
        EventDao eventDao = new EventDao(conn);
        PersonDao personDao = new PersonDao(conn);
        assertFalse(eventDao.findAll("MK").isEmpty());
        assertFalse(personDao.findAll("MK").isEmpty());
        database.closeConnection(false);

    }

    @Test
    public void FillFail() throws DataAccessException {
        FillRequest fillRequest = new FillRequest("Non-existant",3);
        Fill fill = new Fill();
        FillResult fillResult = fill.fillService(fillRequest);
        assertFalse(fillResult.isSuccess());



    }

    @Test
    public void RegisterPass() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("user21","421","ekqq","fart","drat","m");
        Register register = new Register();
        RegisterResult registerResult = register.registerService(registerRequest);
        assertTrue(registerResult.isSuccess());
        database.openConnection();
        Connection conn = database.getConnection();
        EventDao eventDao = new EventDao(conn);
        PersonDao personDao = new PersonDao(conn);
        UserDao userDao = new UserDao(conn);
        assertFalse(eventDao.findAll("user21").isEmpty());
        assertFalse(personDao.findAll("user21").isEmpty());
        assertNotNull(userDao.find("user21"));
        database.closeConnection(false);

    }

    @Test
    public void RegisterFail() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("MK","421","ekqq","fart","drat","m");
        Register register = new Register();
        RegisterResult registerResult = register.registerService(registerRequest);
        assertFalse(registerResult.isSuccess());
    }

    @Test
    public void LoadPass() throws DataAccessException{
        EventMod e = new EventMod("i90", "Spencer", "SPence1",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        List<EventMod> array = new ArrayList<EventMod>();
        array.add(e);

        LoadRequest loadRequest = new LoadRequest(new ArrayList<UserMod>(),new ArrayList<PersonMod>(), array);
        Load load = new Load();
        LoadResult loadResult = load.loadService(loadRequest);
        assertTrue(loadResult.isSuccess());
        EventDao eventDao = new EventDao(database.getConnection());
        List<EventMod> events = eventDao.findAll("Spencer");
        database.closeConnection(false);
        assertTrue(events.size() == 1);

    }

    @Test
    public void LoadFail() throws DataAccessException{
        EventMod e = new EventMod(null, "Spencer", "SPence1",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        List<EventMod> array = new ArrayList<EventMod>();
        array.add(e);

        LoadRequest loadRequest = new LoadRequest(new ArrayList<UserMod>(),new ArrayList<PersonMod>(), array);
        Load load = new Load();
        LoadResult loadResult = load.loadService(loadRequest);
        assertFalse(loadResult.isSuccess());

    }

    @Test
    public void LoginPass() throws Exception{
        LoginRequest loginRequest = new LoginRequest("MK","1234");
        Login login = new Login();
        LoginResult loginResult = login.loginService(loginRequest);
        assertTrue(loginResult.isSuccess());
        assertEquals("abcd", loginResult.getPersonID());
    }

    @Test
    public void LoginFail() throws Exception{
        LoginRequest loginRequest = new LoginRequest("Non existant","1234");
        Login login = new Login();
        LoginResult loginResult = login.loginService(loginRequest);
        assertFalse(loginResult.isSuccess());
    }








}
