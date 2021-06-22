package Utilities;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Utilities {

    private List<String> femaleNames;
    private List<String> maleNames;
    private List<String> lastNames;
    private List<Location> locations;

    private Random randomNum;

    public Utilities(){
        randomNum = new Random();
        createAll();

    }

    public List<String> parse(String file){

        List<String> array = null;
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            Gson gson = new Gson();
            Names names = gson.fromJson(bufferedReader,Names.class);
            array = names.getData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }

    public List<Location> parseLocations(String file){

        List<Location> array = null;
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)){

            Gson gson = new Gson();
            Locations locations = gson.fromJson(bufferedReader,Locations.class);
            array = locations.getData();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;

    }

    public void createAll(){
        femaleNames = parse("json/fnames.json");
        maleNames = parse("json/mnames.json");
        lastNames = parse("json/snames.json");
        locations = parseLocations("json/locations.json");
    }

    public String getRandomFname(){

        int index = randomNum.nextInt(femaleNames.size());
        return femaleNames.get(index);
    }

    public String getRandomSname(){

        int index = randomNum.nextInt(lastNames.size());
        return lastNames.get(index);
    }

    public String getRandomMname(){

        int index = randomNum.nextInt(maleNames.size());
        return maleNames.get(index);
    }

    public Location getRandomLocation(){

        int index = randomNum.nextInt(locations.size());
        return locations.get(index);
    }

/**
    public  void main(String[] args){

        createAll();

        for(int i = 0; i < femaleNames.size(); i++){
            System.out.println(femaleNames.get(i));
        }


        for(int i = 0; i < maleNames.size(); i++){
            System.out.println(maleNames.get(i));
        }

        for(int i = 0; i < lastNames.size(); i++){
            System.out.println(lastNames.get(i));
        }

        for(int i = 0; i < locations.size(); i++){
            System.out.println(locations.get(i).toString());
        }

        System.out.println("-------------------------------------------------");
        System.out.println(getRandomLocation().toString());
    }
*/
}
