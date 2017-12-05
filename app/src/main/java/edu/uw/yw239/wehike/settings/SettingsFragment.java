package edu.uw.yw239.wehike.settings;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.MyApplication;
import edu.uw.yw239.wehike.posts.CreatePostActivity;
import edu.uw.yw239.wehike.profile.EditProfileActivity;
import edu.uw.yw239.wehike.signin.SignInActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static final String Settings_Fragment_Tag = "Settings_Fragment_Tag";

    private ImageView pfPhoto;
    private EditText name;
    private EditText phoneNum;
    private EditText email;
    private EditText facebookUrl;
    private EditText twitterUrl;

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

        //// TODO: 12/3/17 set imageview and textview after getting the data from the database

        pfPhoto = (ImageView) getActivity().findViewById(R.id.iv_profile_photo);
        name = (EditText) getActivity().findViewById(R.id.et_profile_name);
        phoneNum = (EditText) getActivity().findViewById(R.id.et_profile_phone);
        email = (EditText) getActivity().findViewById(R.id.et_profile_email);
        facebookUrl = (EditText) getActivity().findViewById(R.id.et_profile_facebook);
        twitterUrl = (EditText) getActivity().findViewById(R.id.et_profile_twitter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        View signOutButton = root.findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return root;
    }

    private void signOut() {
        AccountInfo.clearAccountInfo();

        Intent intent = new Intent(this.getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_settings :

                // go to the edit profile activity
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveProfile(){
        pfPhoto = (ImageView) getActivity().findViewById(R.id.iv_profile_photo);
        name = (EditText) getActivity().findViewById(R.id.et_profile_name);
        phoneNum = (EditText) getActivity().findViewById(R.id.et_profile_phone);
        email = (EditText) getActivity().findViewById(R.id.et_profile_email);
        facebookUrl = (EditText) getActivity().findViewById(R.id.et_profile_facebook);
        twitterUrl = (EditText) getActivity().findViewById(R.id.et_profile_twitter);


    }
}
