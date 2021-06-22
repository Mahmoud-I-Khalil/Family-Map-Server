package Service;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.PersonDao;
import Model.EventMod;
import Model.PersonMod;
import Request.PersonRequest;
import Result.EventResult;
import Result.PersonResult;

import java.sql.Connection;
import java.util.List;

public class Person {

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public PersonResult personService(PersonRequest request){
        PersonResult personResult = null;
        Database db = new Database();
        PersonMod personMod;
        PersonDao personDao;

        try {
            Connection conn = db.getConnection();
            personDao = new PersonDao(conn);
            List<PersonMod> persons = personDao.findAll(request.getUsername());
            if(persons != null && persons.size()>0){
                personResult = new PersonResult(persons,true,null);
            }

            else{
                personResult = new PersonResult(false,"Error: No event was found");
            }

            db.closeConnection(false);

        } catch (DataAccessException e) {
            e.printStackTrace();
            try {
                db.closeConnection(false);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
            personResult = new PersonResult(false,e.getMessage());
        }

        return personResult;
    }
}
