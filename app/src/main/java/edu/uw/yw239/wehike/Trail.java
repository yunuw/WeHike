package edu.uw.yw239.wehike;

import android.util.Log;
import android.webkit.URLUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by wangchen on 11/27/17.
 */

public class Trail {
    public static final String TAG = "Trail";

    public String name = "";
    public String picUrl = "";
    public Double lat;
    public Double lon;
    public ArrayList<String> actNames = new ArrayList<>();
    public ArrayList<String> actPicUrls = new ArrayList<>();
    public ArrayList<String> actDescriptions = new ArrayList<>();
    public ArrayList<Double> actRatings = new ArrayList<>();

    public static List<Trail> parseTrailAPI(JSONObject response){
        ArrayList<Trail> trails = new ArrayList<Trail>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            JSONArray jsontrails = response.getJSONArray("places"); //response.places


            for(int i=0; i<Math.min(jsontrails.length(), 25); i++){
                JSONObject trailItemObj = jsontrails.getJSONObject(i);
                JSONArray actItemArray = trailItemObj.getJSONArray("activities");

                Trail trail = new Trail();

                trail.name = trailItemObj.getString("name");
                trail.lat = trailItemObj.getDouble("lat");
                trail.lon = trailItemObj.getDouble("lon");


                if (actItemArray.length()>0){
                    trail.picUrl = actItemArray.getJSONObject(0).getString("thumbnail");

                    for (int j=0; j<actItemArray.length(); j++){
                        JSONObject actItemObj = actItemArray.getJSONObject(j);
                        trail.actNames.add(actItemObj.getString("name"));
                        trail.actPicUrls.add(actItemObj.getString("thumbnail"));
                        trail.actDescriptions.add(actItemObj.getString("description"));
                        trail.actRatings.add(actItemObj.getDouble("rating"));
                    }
                }

                trails.add(trail);
            } //end for loop
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e); //Android log the error
        }
        Log.v("hahahah",trails.size()+"");
        return trails;
    }
}
