package DataAccess;

import Model.PersonMod;
import Service.Person;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PersonDao {

    private final Connection conn;
    public PersonDao(Connection conn) {this.conn = conn;}

    /**
     * Function that inserts Person
     * @param person
     */
    public void insert(PersonMod person) throws DataAccessException{
        String sql = "INSERT INTO person (personId, username, firstName, lastName, gender, fatherID, " +
                "motherID, spouseID)"+" VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, person.getPersonId());
            if (find(person.getPersonId())!=null) {
                throw new DataAccessException("User exists");
            }
            stmt.setString(2, person.getUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            //incorrect gender input
            if(!person.getGender().equals("m") && !person.getGender().equals("f"))
                throw new DataAccessException("Incorrect format, gender has to be “f” or “m”");
            stmt.setString(6, person.getFatherId());
            stmt.setString(7, person.getMotherId());
            stmt.setString(8, person.getSpouseId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Function that finds a specific person Id
     * @param personId
     * @return
     */
    public PersonMod find(String personId) throws DataAccessException {
        PersonMod person = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM person WHERE personID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new PersonMod(rs.getString("personId"), rs.getString("username"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));
                return person;
            }
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        }

    }

    /**
     * Function that finds all perons based on username
     * @param username
     * @return
     */
    public List<PersonMod> findAll(String username) throws DataAccessException {

        List<PersonMod> persons = new ArrayList<PersonMod>();
        ResultSet rs = null;
        String sql = "SELECT * FROM person WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                PersonMod person = new PersonMod(rs.getString("personId"), rs.getString("username"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("fatherID"),
                        rs.getString("motherID"), rs.getString("spouseID"));
                persons.add(person);
            }

            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding persons[]");
        }

    }

    /**
     * Function that perosns with specific Id
     * @param username
     */
    public void removeBasedOnUser(String username) throws DataAccessException {
        System.out.println("Removing From person all persons related ot the User");

        String sql = "DELETE FROM person where username = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("clear Person Table failed"); }

    }

    /**
     * Function that removes all Persons
     */
    public void removeAll() throws DataAccessException{

        String sql = "DELETE FROM person";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("clear Person Table failed"); }
    }

}
