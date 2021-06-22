package Service;

import DataAccess.*;
import Result.ClearResult;

import java.sql.Connection;

public class Clear {

    /**
     * Function that clears everything
     * @return
     */
    public ClearResult clearService(){

        Database db = new Database();
        ClearResult result = null;
        UserDao uDao;
        EventDao eDao;
        PersonDao pDao;
        AuthTokenDoa aDao;
        try {
            Connection connection = db.getConnection();

            db.clearTables();
            db.closeConnection(true);

        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
            result= new ClearResult(false, "Internal server error");

            try{
                db.closeConnection(false);
            }catch (DataAccessException e2){
                result = new ClearResult(false, e2.getMessage());
                return result;
            }

            return result;
        }
        result = new ClearResult(true, "Clear succeeded");
        return result;
    }

}
