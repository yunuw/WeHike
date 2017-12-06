package edu.uw.yw239.wehike.trails;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchen on 11/27/17.
 */

public class Trail implements Parcelable{
    public static final String TAG = "Trail";

    public String name = "";
    public String picUrl = "";
    public Double lat;
    public Double lon;
    public ArrayList<String> actNames = new ArrayList<>();
    public ArrayList<String> actPicUrls = new ArrayList<>();
    public ArrayList<String> actDescriptions = new ArrayList<>();
    public ArrayList<Double> actRatings = new ArrayList<>();

    public Trail(){

    }

    public static List<Trail> parseTrailAPI(JSONObject response){
        ArrayList<Trail> trails = new ArrayList<Trail>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            JSONArray jsontrails = response.getJSONArray("places"); //response.places


            for(int i=0; i<jsontrails.length(); i++){
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
                        if (!actItemObj.getString("thumbnail").equals("null")){
                            trail.actNames.add(actItemObj.getString("name"));
                            trail.actPicUrls.add(actItemObj.getString("thumbnail"));
                            trail.actDescriptions.add(actItemObj.getString("description"));
                            trail.actRatings.add(actItemObj.getDouble("rating"));
                        }

                    }
                }
                if (trail.actPicUrls.size()!=0){
                    trail.picUrl = trail.actPicUrls.get(0);
                    trails.add(trail);
                }
            } //end for loop
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e); //Android log the error
        }
        Log.v("hahahah",trails.size()+"");
        return trails;
    }

    protected Trail(Parcel in) {
        name = in.readString();
        picUrl = in.readString();
        lat = in.readByte() == 0x00 ? null : in.readDouble();
        lon = in.readByte() == 0x00 ? null : in.readDouble();
        if (in.readByte() == 0x01) {
            actNames = new ArrayList<String>();
            in.readList(actNames, String.class.getClassLoader());
        } else {
            actNames = null;
        }
        if (in.readByte() == 0x01) {
            actPicUrls = new ArrayList<String>();
            in.readList(actPicUrls, String.class.getClassLoader());
        } else {
            actPicUrls = null;
        }
        if (in.readByte() == 0x01) {
            actDescriptions = new ArrayList<String>();
            in.readList(actDescriptions, String.class.getClassLoader());
        } else {
            actDescriptions = null;
        }
        if (in.readByte() == 0x01) {
            actRatings = new ArrayList<Double>();
            in.readList(actRatings, Double.class.getClassLoader());
        } else {
            actRatings = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(picUrl);
        if (lat == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lat);
        }
        if (lon == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(lon);
        }
        if (actNames == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(actNames);
        }
        if (actPicUrls == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(actPicUrls);
        }
        if (actDescriptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(actDescriptions);
        }
        if (actRatings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(actRatings);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trail> CREATOR = new Parcelable.Creator<Trail>() {
        @Override
        public Trail createFromParcel(Parcel in) {
            return new Trail(in);
        }

        @Override
        public Trail[] newArray(int size) {
            return new Trail[size];
        }
    };
}
