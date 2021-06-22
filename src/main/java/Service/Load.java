package Service;

import DataAccess.*;
import Model.EventMod;
import Model.PersonMod;
import Model.UserMod;
import Request.LoadRequest;
import Result.LoadResult;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.List;

public class Load {

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public LoadResult loadService(LoadRequest request){
        Database database = new Database();
        LoadResult loadResult = null;



        List<UserMod> userMods = request.getUsers();
        List<PersonMod> personMods = request.getPersons();
        List<EventMod> eventMods = request.getEvents();

        UserDao userDao;
        EventDao eventDao;
        PersonDao personDao;

        try{

            Connection connection = database.getConnection();
            //SQL was getting locked for weird reason, when I added this it stopped locking
            Thread.sleep(100);
            database.clearTables();
            userDao = new UserDao(connection);
            eventDao = new EventDao(connection);
            personDao = new PersonDao(connection);

            for(int i = 0; i < userMods.size(); i++){
                userDao.insert(userMods.get(i));
            }

            for(int i = 0; i < eventMods.size(); i++){
                eventDao.insert(eventMods.get(i));
            }

            for(int i = 0; i < personMods.size(); i++){
                personDao.insert(personMods.get(i));
            }

            database.closeConnection(true);
            String message = "Successfully added " + userMods.size() + " users, " + personMods.size()
                    + " persons, and " + eventMods.size() + " events to the database.";
            loadResult = new LoadResult(true,message);

        } catch (DataAccessException | InterruptedException e) {
            try {
                database.closeConnection(false);
                loadResult = new LoadResult(false, "Error: data access exception while loading Load Result");
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
            e.printStackTrace();
        }

        return loadResult;
    }
}
