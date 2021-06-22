package passoff;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.UserMod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffmodels.User;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Database db;
    private UserDao userDao;
    private UserMod user;
    private UserMod userNotExistant;

    @BeforeEach
    public void setUp() throws DataAccessException
    {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new event with random data
        user = new UserMod("Jaafar", "12345", "mkhalil2byu@gmail.com",
                "Mahmoud", "Khalil", "m", "8765678");
        userNotExistant = new UserMod("Khalil", "5352", "fa@gmail.com",
                "MKK", "gaie", "f", "421");
        //Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the EventDAO so it can access the database
        userDao = new UserDao(conn);
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
        //While insert returns a bool we can't use that to verify that our function actually worked
        //only that it ran without causing an error
        userDao.insert(user);
        //So lets use a find method to get the event that we just put in back out
        UserMod compareTest = userDao.find(user.getUserName());
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertEquals(user, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        //lets do this test again but this time lets try to make it fail
        //if we call the method the first time it will insert it successfully
        userDao.insert(user);
        //but our sql table is set up so that "eventID" must be unique. So trying to insert it
        //again will cause the method to throw an exception
        //Note: This call uses a lambda function. What a lambda function is is beyond the scope
        //of this class. All you need to know is that this line of code runs the code that
        //comes after the "()->" and expects it to throw an instance of the class in the first parameter.
        assertThrows(DataAccessException.class, ()-> userDao.insert(user));
    }

    @Test
    public void findPass() throws DataAccessException {

        userDao.insert(user);
        UserMod existingUser = userDao.find(user.getUserName());
        assertNotNull(existingUser);
        assertEquals(user, existingUser);
    }

    @Test
    public void findFail() throws DataAccessException {

        assertNull(userDao.find(userNotExistant.getUserName()));
    }

    @Test
    public void clearPass() throws DataAccessException{
        userDao.insert(user);
        userDao.removeAll();
        assertNull(userDao.find("kan fi djeji "));

    }




}
