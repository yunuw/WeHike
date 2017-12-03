package edu.uw.yw239.wehike.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.posts.CreatePostActivity;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView pfPhoto;
    private EditText name;
    private EditText phoneNum;
    private EditText email;
    private EditText facebookUrl;
    private EditText twitterUrl;
    private Button saveButton;
    private Button cancelButton;

    public final static String BACK_TO_SETTINGS_KEY = "back to fragment key";
    public final static int BACK_TO_SETTINGS_VALUE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pfPhoto = (ImageView) findViewById(R.id.iv_edit_profile_photo);
        name = (EditText) findViewById(R.id.et_edit_profile_name);
        phoneNum = (EditText) findViewById(R.id.et_edit_profile_phone);
        email = (EditText) findViewById(R.id.et_edit_profile_email);
        facebookUrl = (EditText) findViewById(R.id.et_edit_profile_facebook);
        twitterUrl = (EditText) findViewById(R.id.et_edit_profile_twitter);
        saveButton = (Button) findViewById(R.id.save_edit);
        cancelButton = (Button) findViewById(R.id.cancel_edit);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the updated profile data to the dataset
                //// TODO: 12/3/17
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the settings fragment
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);

                intent.putExtra(BACK_TO_SETTINGS_KEY, BACK_TO_SETTINGS_VALUE);
                startActivity(intent);
            }
        });
    }
}
