package edu.uw.yw239.wehike;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nan on 11/28/17.
 */

public class Post {
    public static final String TAG = "Post";

    public String name = "Nan Xiao";
    public String picUrl = "";

    public ArrayList<String> userNames = new ArrayList<>();
    public ArrayList<String> postPicUrls = new ArrayList<>();
    public ArrayList<String> postDescriptions = new ArrayList<>();

    /* for json parsing
    public static List<Post> parsePostAPI(JSONObject response){
        ArrayList<Post> posts = new ArrayList<Post>();

        try {
            JSONArray jsonposts = response.getJSONArray("places");


            for(int i=0; i<Math.min(jsonposts.length(), 25); i++){
                JSONObject trailItemObj = jsonposts.getJSONObject(i);
                JSONArray actItemArray = trailItemObj.getJSONArray("activities");

                Post post = new Post();

                post.name = trailItemObj.getString("name");

                if (actItemArray.length()>0){
                    post.picUrl = actItemArray.getJSONObject(0).getString("thumbnail");

                    for (int j=0; j<actItemArray.length(); j++){
                        JSONObject actItemObj = actItemArray.getJSONObject(j);
                        post.userNames.add(actItemObj.getString("name"));
                        post.postPicUrls.add(actItemObj.getString("postpic"));
                        post.postDescriptions.add(actItemObj.getString("description"));
                    }
                }

                posts.add(post);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing json", e); //Android log the error
        }
        Log.v("hahahah",posts.size()+"");
        return posts;
    }*/
}