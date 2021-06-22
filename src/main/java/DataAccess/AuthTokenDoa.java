package DataAccess;

import Model.AuthTokenMod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthTokenDoa {

    private final Connection conn;
    public AuthTokenDoa(Connection conn){
        this.conn = conn;
    }

    /**
     * Function that inserts AuthMod
     * @param token
     */
    public void insert(AuthTokenMod token) throws DataAccessException{
        String sql = "INSERT INTO AuthToken(username, authToken) "+"VALUES(?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token.getUserName());
            stmt.setString(2, token.getToken());
            if (find(token.getToken())!=null) {
                throw new DataAccessException("Authentication already exists");
            }


            stmt.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error in the token insertion");
        }

    }

    /**
     * function that searches and returns the Token
     * @param authToken
     * @return
     */
    public AuthTokenMod find(String authToken) throws DataAccessException{

        AuthTokenMod authTokenMod = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthToken WHERE authToken = ?;";
        try ( PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, authToken);
            rs = stmt.executeQuery();
            if(rs.next()){
                authTokenMod = new AuthTokenMod(rs.getString("username"), rs.getString("authToken"));
                return authTokenMod;
            }
            else {
                return null;
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error in finding Token");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * function that removes an Auth Token based on the username
     * @param username
     */
    public void remove(String username){}

    /**
     * Function that removes all AuthTokens
     */
    public void removeAll() throws DataAccessException{
        String sql = "DELETE FROM AuthToken";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Clear AuthToken table failed");
        }
    }
}
