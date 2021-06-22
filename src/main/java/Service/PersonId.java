package Service;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import DataAccess.PersonDao;
import Model.EventMod;
import Model.PersonMod;
import Request.PersonIdRequest;
import Result.EventIdResult;
import Result.PersonIdResult;

import java.sql.Connection;

public class PersonId {

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public PersonIdResult personIdService(PersonIdRequest request){
        
        PersonIdResult personIdResult = null;
        Database db = new Database();
        PersonMod personObj;
        PersonDao personDao;

        try {
            Connection conn = db.getConnection();
            personDao = new PersonDao(conn);
            personObj = personDao.find(request.getPersonID());
            if(personObj != null && personObj.getUsername().equals(request.getUsername())){
                personIdResult = new PersonIdResult(personObj.getUsername(), personObj.getPersonId(), personObj.getFirstName(), personObj.getLastName(),personObj.getGender(),personObj.getFatherId(),personObj.getMotherId(),personObj.getSpouseId(),true,null);
            }

            else{
                personIdResult = new PersonIdResult(false,"Error: No person was found");
            }

            db.closeConnection(false);

        } catch (DataAccessException e) {
            e.printStackTrace();
            try {
                db.closeConnection(false);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
            personIdResult = new PersonIdResult(false,e.getMessage());
        }

        return personIdResult;
        
    }
}
