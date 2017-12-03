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
    private String description;
    private long createdDate;

    public Post(){
        this.createdDate = new Date().getTime();
    }

    public void setDescription(String description) {
        this.description = description;
    }
}