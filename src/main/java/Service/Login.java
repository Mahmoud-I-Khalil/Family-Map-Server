package Service;

import DataAccess.*;
import Model.*;
import Request.LoadRequest;
import Request.LoginRequest;
import Result.LoginResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class Login {

    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public LoginResult loginService(LoginRequest request){

        LoginResult result = null;
        UserDao userDao;
        AuthTokenDoa authTokenDoa;
        Database database = new Database();
        boolean success = false;

        try {
            Connection conn = database.getConnection();
            userDao = new UserDao(conn);
            authTokenDoa = new AuthTokenDoa(conn);

            UserMod userMod = userDao.find(request.getUsername());
            AuthTokenMod authToken = authTokenDoa.find(request.getUsername());

            if(userMod != null){
                if(userMod.getUserName().equals(request.getUsername())){
                    if(userMod.getPassword().equals(request.getPassword())){
                        success = true;
                        String token = UUID.randomUUID().toString();
                        authToken = new AuthTokenMod(userMod.getUserName(), token);
                        authTokenDoa.insert(authToken);
                        result = new LoginResult(token,userMod.getUserName(), userMod.getPersonId(), success,"Successfully Logged In :)");

                    }
                    else{
                        result = new LoginResult(false,"Error: Password is Incorrect");

                    }
                }
                else{
                    result = new LoginResult(false,"Error: Username is Incorrect");

                }
            }
            else{
                result = new LoginResult(false,"Error: Username is Incorrect");

            }


        } catch (DataAccessException e) {
            e.printStackTrace();
            result = new LoginResult(false,"Error: Internal Server error");


        } finally {
            try {
                if (result.isSuccess()) {
                    database.closeConnection(true);
                } else {
                    database.closeConnection(false);
                }
            }catch (DataAccessException e){
                e.printStackTrace();
            }
        }

        return result;

    }
}
