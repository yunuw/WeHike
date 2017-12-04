package edu.uw.yw239.wehike.posts;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nan on 11/28/17.
 */

public class Post {
    public int postId;
    public String userName;
    public String imageUrl;
    public String userImageUrl;
    public String description;
    public double longitude;
    public double latitude;
    // TODO: change to Date type if it's needed
    public String timestamp;

}