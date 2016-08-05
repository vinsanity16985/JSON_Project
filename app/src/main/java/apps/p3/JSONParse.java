package apps.p3;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinsa_000 on 3/16/2016.
 */
public class JSONParse {

    private JSONArray jArray;
    private ArrayList<Pet> pets;

    public JSONParse(){
        pets = new ArrayList<Pet>();
    }

    /*
    Takes in JSON data as a String and then parses it extracting its data and placing into an ArrayList<Pet>
    @param: String text, JSON data to parse
    @return: ArrayList<Pet>, ArrayList containing data parsed from the original data
     */
    public ArrayList<Pet> getPets(String text){
        try{
            JSONObject jObj = new JSONObject(text);
            jArray = jObj.getJSONArray("pets");

            for(int i = 0; i < jArray.length(); i++){
                JSONObject temp = jArray.getJSONObject(i);
                Pet pet = new Pet();
                pet.setName(temp.getString("name"));
                pet.setPicture(temp.getString("file"));
                pets.add(pet);
            }
            return pets;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
