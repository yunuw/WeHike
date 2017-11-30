package edu.uw.yw239.wehike;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Created by Nan on 11/28/17.
 */

public class CreatePostActivity extends AppCompatActivity {

    protected ImageView pickedImage;
    protected EditText postDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        pickedImage = (ImageView) findViewById(R.id.iv_selected_image);
        postDesc = (EditText) findViewById(R.id.et_post_desc);


        pickedImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

        // Set up the hint status for post description
        postDesc.setHint(getResources().getString(R.string.post_desc_hint));

        postDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    postDesc.setHint(getResources().getString(R.string.post_desc_hint));
                else
                    postDesc.setHint("");
            }
        });


    }

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

}
