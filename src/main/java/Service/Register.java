package Service;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.UserMod;
import Request.FillRequest;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.FillResult;
import Result.LoginResult;
import Result.RegisterResult;

import java.sql.Connection;
import java.util.UUID;

public class Register {

    private final int NUM_GENS = 4;

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public RegisterResult registerService(RegisterRequest request){

        RegisterResult result = null;
        UserMod newUser;
        UserDao userDao;
        Database db = new Database();
        Fill fill = new Fill();

        try{
            Connection conn = db.getConnection();
            userDao = new UserDao(conn);
            newUser = new UserMod(request.getUsername(),request.getPassword(),request.getEmail(),request.getFirstName(),request.getLastName(),request.getGender(), UUID.randomUUID().toString());
            if(userDao.find(request.getUsername())!= null) {
                result = new RegisterResult(false,"Error: User Already exists");
                db.closeConnection(false);
                return result;
                //throw new DataAccessException("Error: User Already exists");
            }
            try{
                userDao.insert(newUser);
                db.closeConnection(true);
                FillResult fillResult = fill.fillService(new FillRequest(newUser.getUserName(),NUM_GENS));
                if(fillResult.isSuccess()){
                    Login login = new Login();
                    LoginRequest loginRequest = new LoginRequest(newUser.getUserName(),newUser.getPassword());
                    LoginResult loginResult = login.loginService(loginRequest);
                    if(loginResult.isSuccess()){

                        result = new RegisterResult(loginResult.getAuthtoken(),loginResult.getUsername(),loginResult.getPersonID(),true, null);
                    }

                    else{
                        result = new RegisterResult(false,"Error : login not successful");

                        //throw new DataAccessException("Error : login not successful");
                    }
                }

                else{
                    result = new RegisterResult(false,"Error: Registration was not succesfull");
                    //throw new DataAccessException("Error: Registration was not succesfull");
                }

            }catch (DataAccessException e){
                e.printStackTrace();
            }


        }catch (DataAccessException e){
            e.printStackTrace();
            try{
                db.closeConnection(false);

            }catch (DataAccessException e1){
                e1.printStackTrace();

            }
        }
        return result;
    }
}
