package Service;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Model.EventMod;
import Request.EventRequest;
import Result.EventIdResult;
import Result.EventResult;

import java.sql.Connection;
import java.util.List;

public class Event {

    /**
     * Takes request and shows the result
     * @param r
     * @return
     */
    public EventResult eventService(EventRequest r){
        EventResult eventResult = null;
        Database db = new Database();
        EventMod eventObj;
        EventDao eventDao;

        try {
            Connection conn = db.getConnection();
            eventDao = new EventDao(conn);
            List<EventMod> events = eventDao.findAll(r.getUsername());
            if(events != null && events.size()>0){
                eventResult = new EventResult(events,true,null);
            }

            else{
                eventResult = new EventResult(false,"Error: No event was found");
            }

            db.closeConnection(false);

        } catch (DataAccessException e) {
            e.printStackTrace();
            try{
                db.closeConnection(false);
            }catch (DataAccessException e1){
                e1.printStackTrace();
            }
            eventResult = new EventResult(false,e.getMessage());
        }

        return eventResult;
    }
}
