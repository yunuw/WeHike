package edu.uw.yw239.wehike.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.yw239.wehike.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView userPhoto;
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

        userPhoto = (ImageView)findViewById(R.id.iv_user_photo);
        userName = (TextView)findViewById(R.id.et_user_name);
        userPhoneNum = (TextView)findViewById(R.id.et_user_phone);
        userEmail = (TextView)findViewById(R.id.et_user_email);
        userFacebookUrl = (TextView)findViewById(R.id.et_user_facebook);
        userTwitterUrl = (TextView)findViewById(R.id.et_user_twitter);
        
    }
}
