package edu.uw.yw239.wehike.settings;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.MyApplication;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.posts.CreatePostActivity;
import edu.uw.yw239.wehike.posts.Post;
import edu.uw.yw239.wehike.posts.PostsFragment;
import edu.uw.yw239.wehike.profile.EditProfileActivity;
import edu.uw.yw239.wehike.profile.Profile;
import edu.uw.yw239.wehike.signin.SignInActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static final String Settings_Fragment_Tag = "Settings_Fragment_Tag";

    private NetworkImageView pfPhoto;
    private TextView name;
    private TextView phoneNum;
    private TextView email;
    private TextView facebookUrl;
    private TextView twitterUrl;

    private Profile profile;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        View signOutButton = root.findViewById(R.id.sign_out);
        Button editProfileButton = root.findViewById(R.id.edit_profile);

        pfPhoto = (NetworkImageView) root.findViewById(R.id.iv_profile_photo);
        name = (TextView) root.findViewById(R.id.et_profile_name);

        pfPhoto.setDefaultImageResId(R.mipmap.default_profile_image);

        profile = new Profile();
        getProfile();
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void getProfile() {
        profile.userName = AccountInfo.getCurrentUserName();
        final Resources resources = this.getResources();
        final String backendPrefix = resources.getString(R.string.backend_prefix);

        String urlString = String.format("%s/users/get?userName=%s", backendPrefix, profile.userName);
        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)  {
                        try{
                            profile.imageUrl = response.getString("photoUrl");

                            if(profile.imageUrl != null && profile.imageUrl != "null" && profile.imageUrl != "") {
                                pfPhoto.setImageUrl(profile.imageUrl, RequestSingleton.getInstance(MyApplication.getContext()).getImageLoader());
                            }
                            else{
                                pfPhoto.setDefaultImageResId(R.mipmap.default_profile_image);
                            }
                            name.setText(profile.userName);

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

        RequestSingleton.getInstance(this.getContext()).add(request);
    }

    private void signOut() {
        AccountInfo.clearAccountInfo();

        Intent intent = new Intent(this.getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
