package Service;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Model.EventMod;
import Request.EventIdRequest;
import Result.EventIdResult;

import javax.xml.crypto.Data;
import java.sql.Connection;

public class EventID {

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public EventIdResult eventIdService(EventIdRequest request){
        EventIdResult eventIdResult = null;
        Database db = new Database();
        EventMod eventObj;
        EventDao eventDao;

        try {
            Connection conn = db.getConnection();
            eventDao = new EventDao(conn);
            eventObj = eventDao.find(request.getEventId());
            if(eventObj != null && request.getUsername().equals(eventObj.getAssociatedUsername())){
                eventIdResult = new EventIdResult(eventObj.getAssociatedUsername(), eventObj.getEventId(), eventObj.getPersonId(),
                        eventObj.getLatitude(),eventObj.getLongitude(),eventObj.getCountry(),eventObj.getCity(),eventObj.getEventType(),eventObj.getYear(),true);

            }

            else{
                eventIdResult = new EventIdResult(false,"Error: No event was found");
            }

            db.closeConnection(false);

        } catch (DataAccessException e) {
            e.printStackTrace();
            try {
                db.closeConnection(false);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
            eventIdResult = new EventIdResult(false,e.getMessage());
        }

        return eventIdResult;
    }
}

