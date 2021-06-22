package Service;

import DataAccess.*;

import Model.EventMod;
import Model.PersonMod;
import Model.UserMod;
import Request.EventRequest;
import Request.FillRequest;
import Result.EventResult;
import Result.FillResult;

import Result.PersonResult;
import Utilities.*;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Random;
import java.util.UUID;


public class Fill {

    private Utilities randomJsons;
    private Random ran = new Random();
    private String associatedUsername = null;
    private int numPeople = 0;
    private int numEvents = 0;
    private Database database = new Database();

    public Fill(){
        randomJsons = new Utilities();
        randomJsons.createAll();
    }


    /**
     * Takes request and shows the result
     * @param request
     * @return
     */
    public FillResult fillService(FillRequest request){

        UserMod userObj;
        UserDao userDao;
        PersonMod personObj;
        PersonDao personDao;
        EventMod eventObj;
        EventDao eventDao;
        FillResult fillResult = null;

        try {
            Connection conn = database.getConnection();
            userDao = new UserDao(conn);
            personDao = new PersonDao(conn);
            eventDao = new EventDao(conn);

            UserMod userMod = userDao.find(request.getUsername());
            String motherID =UUID.randomUUID().toString();
            String fatherID = UUID.randomUUID().toString();
            int birthYear;
            if(userMod != null){
                personObj = new PersonMod(userMod.getPersonId(),userMod.getUserName(), userMod.getFirstName(), userMod.getLastName(),
                        userMod.getGender(), fatherID, motherID, null);
                // Clearing the things related to USER; Events and Persons
                personDao.removeBasedOnUser(userMod.getUserName());
                eventDao.removeBasedOnUser(userMod.getUserName());


                try{
                    personDao.insert(personObj);
                    numPeople++;
                    int baseYear = 1813;
                    birthYear = ran.nextInt(238)+baseYear;
                    Location randomLocation = randomJsons.getRandomLocation();
                    eventObj = new EventMod(UUID.randomUUID().toString(),personObj.getUsername(),personObj.getPersonId(),randomLocation.getLatitude(),randomLocation.getLongitude(),
                            randomLocation.getCountry(),randomLocation.getCity(),"birth", birthYear);
                    eventDao.insert(eventObj);
                    numEvents++;
                    database.closeConnection(true);
                    associatedUsername = personObj.getUsername();
                    baseYear -= 13;
                    generate(fatherID,motherID, personObj.getLastName(), birthYear,baseYear,personObj,request.getGenerations());

                    fillResult = new FillResult(true,"Successfully added "+ numPeople + " persons and " + numEvents + " events to the database.");
                    //System.out.println("Successfully added "+ numPeople + " persons and " + numEvents + " events to the database.");

                }catch (DataAccessException e){
                    e.printStackTrace();
                }
            }
            else{
                fillResult = new FillResult(false,"Error: Username is Incorrect");
                database.closeConnection(false);
            }



        } catch (DataAccessException e) {
            e.printStackTrace();
            fillResult = new FillResult(false,"Error: Internal Server error");


        }

        return fillResult;
    }

    public void generate(String fatherId, String motherId, String lastName, int birthYear, int baseYear, PersonMod personMod, int generation){
        //Setting an upper limti for creating random ages;
        int upperYear = 2050;

        EventMod eventObj;
        PersonMod mother;
        PersonMod father;
        PersonDao personDao;
        EventDao eventDao;




        try {
            Connection conn = database.getConnection();
            personDao = new PersonDao(conn);
            eventDao = new EventDao(conn);

            String flastName = randomJsons.getRandomSname();
            String mlastName = lastName;
            String femaleName = randomJsons.getRandomFname();
            String maleName = randomJsons.getRandomMname();

            String mMotherId = null;
            String mFatherId = null;
            String fFatherId = null;
            String fMotherId = null;

            // Creating Mom
            if (generation>1){
                mMotherId = UUID.randomUUID().toString();
                mFatherId = UUID.randomUUID().toString();
                fFatherId = UUID.randomUUID().toString();
                fMotherId = UUID.randomUUID().toString();
            }

            mother = new PersonMod(motherId,associatedUsername,femaleName,flastName,"f", mFatherId,mMotherId,fatherId);
            personDao.insert(mother);
            numPeople++;

            //Creating Father

            father = new PersonMod(fatherId,associatedUsername,maleName,mlastName,"m",fFatherId,fMotherId,motherId);
            personDao.insert(father);
            numPeople++;

            //birth event For Father and Mother
            int count;

            //For Father
            Location location = randomJsons.getRandomLocation();
            // father Year is the father's birth year
            int fatherYear = ran.nextInt(Math.abs(birthYear- 16 -baseYear))+(baseYear);
            count = 0;
            while((birthYear - fatherYear)>40){
                if(count >20){
                    fatherYear = birthYear-16;
                    break;
                }
                fatherYear = ran.nextInt(Math.abs(birthYear- 16 -baseYear))+(baseYear);
                System.out.println("DadBirth");
                count++;
            }

            eventObj = new EventMod(UUID.randomUUID().toString(),father.getUsername(),father.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"birth",fatherYear);
            eventDao.insert(eventObj);

            //For Mother
            count = 0;
            location = randomJsons.getRandomLocation();
            //MotherYear is mother's birth Year
            int motherYear = ran.nextInt(Math.abs(birthYear- 14 -baseYear))+(baseYear);
            while((birthYear - motherYear)>45){
                if(count >20){
                    motherYear = birthYear-14;
                    break;
                }
                motherYear = ran.nextInt(Math.abs(birthYear- 14 -baseYear))+(baseYear);
                System.out.println("MotherBirth");
                count++;
            }

            eventObj = new EventMod(UUID.randomUUID().toString(),mother.getUsername(),mother.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"birth",motherYear);
            eventDao.insert(eventObj);

            //marriage event
            location = randomJsons.getRandomLocation();
            int marriage = (Math.max(motherYear,fatherYear))+14;
            //mother
            eventObj = new EventMod(UUID.randomUUID().toString(),mother.getUsername(),mother.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"marriage",marriage);
            eventDao.insert(eventObj);
            //Father
            eventObj = new EventMod(UUID.randomUUID().toString(),father.getUsername(),father.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"marriage",marriage);
            eventDao.insert(eventObj);

            //death event
            //father
            location = randomJsons.getRandomLocation();
            int deathFyear = ran.nextInt(upperYear-birthYear)+(birthYear);
            count = 0;
            while((deathFyear - fatherYear)>100 || deathFyear < birthYear){
                if(count >20){
                    deathFyear = birthYear+1;
                    break;
                }
                deathFyear = ran.nextInt(upperYear-birthYear)+(birthYear);
                System.out.println("FatherDeath");
                count++;

            }

            eventObj = new EventMod(UUID.randomUUID().toString(),father.getUsername(),father.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"death",deathFyear);
            eventDao.insert(eventObj);

            //For Mother
            location = randomJsons.getRandomLocation();
            count = 0;
            int deathMyear = ran.nextInt(upperYear-birthYear)+(birthYear);
            while((deathMyear - motherYear)>100 || deathMyear < birthYear){
                if(count >20){
                    deathMyear = birthYear+1;
                    break;
                }
                deathMyear = ran.nextInt(upperYear-birthYear)+(birthYear);
                System.out.println("MotherDeath");
                count++;
            }

            eventObj = new EventMod(UUID.randomUUID().toString(),mother.getUsername(),mother.getPersonId(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(),"death",deathMyear);
            eventDao.insert(eventObj);

            numEvents = numEvents + 6;

            database.closeConnection(true);

            //recursive
            baseYear = baseYear - 25;

            generation --;
            if(generation>0) {
                generate(fFatherId, fMotherId, mlastName, fatherYear, baseYear, father, generation);
                generate(mFatherId, mMotherId, randomJsons.getRandomSname(), motherYear, baseYear, mother, generation);
            }



        } catch (DataAccessException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
        }


    }
}
