package DataAccess;

import Model.EventMod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDao {

    private final Connection conn;
    public EventDao(Connection conn){
        this.conn = conn;
    }

    /**
     * Insert Event function
     * @param event
     */
    public void insert(EventMod event) throws DataAccessException{
        String sql = "INSERT INTO events (eventId, associatedUsername, personId, latitude, longitude, country, city, eventType, year) " + "VALUES(?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, event.getEventId());
            if (find(event.getEventId())!=null) {
                throw new DataAccessException("Event exists");
            }
            stmt.setString(2,event.getAssociatedUsername());
            stmt.setString(3,event.getPersonId());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5,event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9,event.getYear());

            stmt.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error in inserting event");
        }
    }

    /**
     *  Finds a sepecif Event
     * @param eventId
     * @return
     */
    public EventMod find (String eventId) throws DataAccessException{
        EventMod event = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM events WHERE eventId = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, eventId);
            rs = stmt.executeQuery();
            if(rs.next()){
                event = new EventMod(rs.getString("eventId"), rs.getString("associatedUsername"),rs.getString("personId"),
                        rs.getFloat("latitude"), rs.getFloat("longitude"), rs.getString("country"),
                        rs.getString("city"), rs.getString("eventType"),rs.getInt("year"));
                return event;
            }
            else{
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error finding Event");
        }
    }

    /**
     * Finds all events based on Username
     * @param username
     * @return
     */
    public List<EventMod> findAll(String username) throws DataAccessException {

        List<EventMod> event = new ArrayList<EventMod>();
        ResultSet rs = null;
        String sql = "SELECT * FROM events WHERE associatedUsername = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while(rs.next()){
                EventMod event1 = new EventMod(rs.getString("eventId"), rs.getString("associatedUsername"),rs.getString("personId"),
                        rs.getFloat("latitude"), rs.getFloat("longitude"), rs.getString("country"),
                        rs.getString("city"), rs.getString("eventType"),rs.getInt("year"));
                event.add(event1);
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error finding Event");
        }

        return event;

    }

    /**
     * Function that removes a specific event
     * @param associatedUsername
     */
    public void removeBasedOnUser(String associatedUsername) throws DataAccessException {
        System.out.println("Removing From Person all events related ot the User");
        String sql = "DELETE FROM events WHERE associatedUsername = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, associatedUsername);
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error in deleting from events");
        }
    }

    /**
     * Function that removes all Events
     */
    public void removeAll() throws DataAccessException{
        String sql = "DELETE FROM events";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Error in deleting from events");
        }
    }

}
