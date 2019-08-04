package com.example.thainguyen.mapdirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParse {

    private HashMap<String,String> getPlaces (JSONObject jsonObject)
    {
        HashMap<String,String> places= new HashMap<>();
        String nameplace = "-NA-";
        String vicinity = "-NA-";
        String lattiude ="";
        String longtude = "";
        String reference = "";
        try {
            if(!jsonObject.isNull("name"))
            {
                nameplace = jsonObject.getString("name");

            }
            if (!jsonObject.isNull("vicinity"))
            {
                vicinity = jsonObject.getString("vicinity");

            }
            lattiude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference =jsonObject.getString("reference");

            places.put("place_name",nameplace);
            places.put("vicinity",vicinity);
            places.put("lat",lattiude);
            places.put("lng",longtude);
            places.put("reference",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return places;
    }
    private List<HashMap<String,String>> getALlPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String,String>> listNearBy = new ArrayList<>();

        HashMap<String,String> nearByPlace = null;
        for(int i=0;i<count;i++)
        {
            try {
                nearByPlace= getPlaces((JSONObject) jsonArray.get(i));
                listNearBy.add(nearByPlace);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  listNearBy;
    }
    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getALlPlaces(jsonArray);
    }
}
