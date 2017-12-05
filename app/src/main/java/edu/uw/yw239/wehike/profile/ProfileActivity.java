package edu.uw.yw239.wehike.profile;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.MyApplication;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.posts.PostsFragment;
import edu.uw.yw239.wehike.trails.TrailsFragment;

public class ProfileActivity extends AppCompatActivity {
    public static final String USER_NAME_KEY = "USER_NAME_KEY";
    private NetworkImageView userPhoto;
    private TextView userName;
    private TextView userPhoneNum;
    private TextView userEmail;
    private TextView userFacebookUrl;
    private TextView userTwitterUrl;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userPhoto = (NetworkImageView)findViewById(R.id.iv_user_photo);
        userName = (TextView)findViewById(R.id.et_user_name);
        userPhoneNum = (TextView)findViewById(R.id.et_user_phone);
        userEmail = (TextView)findViewById(R.id.et_user_email);
        userFacebookUrl = (TextView)findViewById(R.id.et_user_facebook);
        userTwitterUrl = (TextView)findViewById(R.id.et_user_twitter);

        userPhoto.setDefaultImageResId(R.mipmap.default_profile_image);

        profile = new Profile();

        Intent intent = getIntent();
        if(intent != null) {
            profile.userName = intent.getStringExtra(USER_NAME_KEY);
        }

        getProfile();
    }


    private void getProfile() {
        //profile.userName = AccountInfo.getCurrentUserName();
        final Resources resources = this.getResources();
        final String backendPrefix = resources.getString(R.string.backend_prefix);

        String urlString = String.format("%s/users/get?userName=%s", backendPrefix, profile.userName);
        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)  {
                        try{
                            profile.email = response.getString("email");
                            profile.imageUrl = response.getString("photoUrl");
                            profile.phoneNumber = response.getString("phoneNumber");
                            profile.facebookUrl = response.getString("facebookUrl");
                            profile.twitterUrl = response.getString("twitterUrl");

                            if(profile.imageUrl != null && profile.imageUrl != "null" && profile.imageUrl != "") {
                                userPhoto.setImageUrl(profile.imageUrl, RequestSingleton.getInstance(MyApplication.getContext()).getImageLoader());
                            }
                            else{
                                userPhoto.setDefaultImageResId(R.mipmap.default_profile_image);
                            }
                            userName.setText(profile.userName);
                            userPhoneNum.setText(profile.phoneNumber);
                            userEmail.setText(profile.email);
                            userFacebookUrl.setText(profile.facebookUrl);
                            userTwitterUrl.setText(profile.twitterUrl);

                        }catch (JSONException e) {
                            Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errMsg = "Failed to get profile";
                        if (error.networkResponse != null) {
                            errMsg = errMsg + "\n" + "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                        }

                        Toast.makeText(MyApplication.getContext(), errMsg, Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestSingleton.getInstance(this).add(request);
    }


    private void goBack(){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

        intent.putExtra(MainActivity.FRAGMENT_TO_SELECT_KEY, PostsFragment.Posts_Fragment_Tag);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
