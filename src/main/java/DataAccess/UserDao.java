package DataAccess;

import Model.UserMod;

import java.sql.*;

public class UserDao {

    private final Connection conn;

    public UserDao(Connection conn)
    {
        this.conn = conn;
    }
    /**
     * Function that inserts Users
     * @param user
     */
    public void insert(UserMod user) throws DataAccessException{

        String sql = "INSERT INTO users (username, password, email, firstName, lastName, gender, personID)" + " VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            if(!user.getGender().equals("m") && !user.getGender().equals("f"))
                throw new DataAccessException("Incorrect format, gender has to be “f” or “m” ");
            stmt.setString(7, user.getPersonId());
            if(findId(user.getPersonId())){
                throw new DataAccessException("User already exists");
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting a person into the database");
        }

    }

    public boolean findId( String personId) throws DataAccessException{
        UserMod user;
        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE personID = '" + personId + "'";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            rs = statement.executeQuery();
            if (rs.next()){
                return true;
            }

        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        }
        return false;
    }

    /**
     * Function that inserts Users
     * @param username
     * @return
     */
    public UserMod find(String username) throws DataAccessException{
        UserMod user;
        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new UserMod(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"),
                        rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("personID"));
                return user;
            }
            else{
                //throw new DataAccessException("User not found");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
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
     * Function that removes Users
     * @param username
     */
    public void remove(String username){

    }

    /**
     * Function that removes all users
     */
    public void removeAll() throws DataAccessException{
        String sql = "DELETE FROM users";
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("clear User Table failed"); }
    }


}
