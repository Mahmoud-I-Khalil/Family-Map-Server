package passoff;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.AuthTokenDoa;
import Model.AuthTokenMod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffmodels.User;

import java.sql.Connection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenTest {

    private Database db;
    private AuthTokenDoa authTokenDoa;
    private AuthTokenMod authToken;
    private AuthTokenMod authTokenNot;

    @BeforeEach
    public void setUp() throws DataAccessException
    {

        db = new Database();
        Connection conn = db.getConnection();
        db.clearTables();
        authToken = new AuthTokenMod("MkthePro", UUID.randomUUID().toString());
        authTokenDoa = new AuthTokenDoa(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        //Here we close the connection to the database file so it can be opened elsewhere.
        //We will leave commit to false because we have no need to save the changes to the database
        //between test cases
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {

        authTokenDoa.insert(authToken);
        AuthTokenMod compareTest = authTokenDoa.find(authToken.getToken());
        assertNotNull(compareTest);
        assertEquals(authToken, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {

        authTokenDoa.insert(authToken);
        assertThrows(DataAccessException.class, ()-> authTokenDoa.insert(authToken));
    }

    @Test
    public void findPass() throws DataAccessException {

        authTokenDoa.insert(authToken);
        AuthTokenMod existingToken = authTokenDoa.find(authToken.getToken());
        assertNotNull(existingToken);
        assertEquals(authToken, existingToken);
    }

    @Test
    public void findFail() throws DataAccessException {
        assertNull(authTokenDoa.find(authToken.getToken()));
    }

    @Test
    public void clearPass() throws DataAccessException{
        authTokenDoa.insert(authToken);
        authTokenDoa.removeAll();
        assertNull(authTokenDoa.find(authToken.getToken()));

    }




}
