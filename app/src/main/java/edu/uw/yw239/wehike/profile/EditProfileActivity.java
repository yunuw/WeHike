package edu.uw.yw239.wehike.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.MyApplication;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.common.StorageManager;
import edu.uw.yw239.wehike.posts.CreatePostActivity;
import edu.uw.yw239.wehike.posts.PostsFragment;
import edu.uw.yw239.wehike.settings.SettingsFragment;

public class EditProfileActivity extends AppCompatActivity {

    private NetworkImageView pfPhoto;
    private TextView tvUserName;
    private EditText editPhoneNum;
    private EditText editEmail;
    private EditText editFacebookUrl;
    private EditText editTwitterUrl;
    private Button saveButton;
    private Button cancelButton;
    private Uri imageUri;

    private Profile profile;

    private static final int MAX_IMAGE_SIZE = 10485760;   //10 Mb
    private static final String [] IMAGE_TYPE = new String[]{"jpg", "png", "jpeg", "bmp", "jp2", "psd", "tif", "gif"};

    private ProgressBar imageLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pfPhoto = (NetworkImageView) findViewById(R.id.iv_edit_profile_photo);
        tvUserName = (TextView) findViewById(R.id.et_edit_profile_user_name);
        editPhoneNum = (EditText) findViewById(R.id.et_edit_profile_phone);
        editEmail = (EditText) findViewById(R.id.et_edit_profile_email);
        editFacebookUrl = (EditText) findViewById(R.id.et_edit_profile_facebook);
        editTwitterUrl = (EditText) findViewById(R.id.et_edit_profile_twitter);
        saveButton = (Button) findViewById(R.id.save_edit);
        cancelButton = (Button) findViewById(R.id.cancel_edit);
        imageLoadingProgress = (ProgressBar) findViewById(R.id.pb_photo_loading);

        pfPhoto.setDefaultImageResId(R.mipmap.default_profile_image);

        profile = new Profile();
        profile.userName = AccountInfo.getCurrentUserName();
        tvUserName.setText(profile.userName);

        pfPhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the updated profile data to the dataset
                profile.phoneNumber = editPhoneNum.getText().toString().trim();
                profile.email = editEmail.getText().toString().trim();
                profile.facebookUrl = editFacebookUrl.getText().toString().trim();
                profile.twitterUrl = editTwitterUrl.getText().toString().trim();

                if (imageUri == null
                        && profile.phoneNumber.equals("")
                        && profile.email.equals("")
                        && profile.facebookUrl.equals("")
                        && profile.twitterUrl.equals("")) {
                    Toast.makeText(EditProfileActivity.this, "Nothing to update", Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: Set the value to space (" ") when it's empty string, otherwise server will return bad request as it doesn't understand the URL
                // which looks like http://address/query?param1=&param2=, that missed values after "="
               /* profile.phoneNumber = profile.phoneNumber.equals("") ? " " : profile.phoneNumber;
                profile.email = profile.email.equals("") ? " " : profile.email;
                profile.facebookUrl = profile.facebookUrl.equals("") ? " " : profile.facebookUrl;
                profile.twitterUrl = profile.twitterUrl.equals("") ? " " : profile.twitterUrl;*/

                if(imageUri != null) {
                    attemptSaveProfile();
                }
                else{
                    updateProfile(null, profile.phoneNumber, profile.email, profile.facebookUrl, profile.twitterUrl);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the settings fragment
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);

                intent.putExtra(MainActivity.FRAGMENT_TO_SELECT_KEY, SettingsFragment.Settings_Fragment_Tag);
                startActivity(intent);
            }
        });

        getProfile();
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
                            profile.email = response.getString("email");
                            profile.imageUrl = response.getString("photoUrl");
                            profile.phoneNumber = response.getString("phoneNumber");
                            profile.facebookUrl = response.getString("facebookUrl");
                            profile.twitterUrl = response.getString("twitterUrl");

                            if(profile.imageUrl != null && profile.imageUrl != "") {
                                pfPhoto.setImageUrl(profile.imageUrl, RequestSingleton.getInstance(MyApplication.getContext()).getImageLoader());
                            }
                            else {
                                pfPhoto.setDefaultImageResId(R.mipmap.default_profile_image);
                            }
                            tvUserName.setText(profile.userName);
                            editPhoneNum.setText(profile.phoneNumber);
                            editEmail.setText(profile.email);
                            editFacebookUrl.setText(profile.facebookUrl);
                            editTwitterUrl.setText(profile.twitterUrl);

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

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (isImageFileValid(imageUri)) {
                this.imageUri = imageUri;
            }

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted
                loadImageToImageView();
            }
        }
    }


    protected boolean isImageFileValid(Uri imageUri) {
        boolean result = false;
        int message = R.string.error_image_uri_null;

        if (imageUri != null) {
            if (isImageUri(imageUri, this)) {
                File imageFile = new File(imageUri.getPath());

                // Check the size of the image
                if (imageFile.length() > MAX_IMAGE_SIZE) {
                    message = R.string.error_image_oversized;
                } else {
                    result = true;
                }
            } else {
                message = R.string.error_image_wrong_file_type;
            }
        }

        if (!result) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    message, Snackbar.LENGTH_LONG);
            snackbar.show();
            imageLoadingProgress.setVisibility(View.GONE);
        }

        return result;
    }

    public static boolean isImageUri(Uri uri, Context context) {
        String mType = context.getContentResolver().getType(uri);

        if (mType != null) {
            return mType.contains("image");
        } else {
            String filenameArray[] = uri.getPath().split("\\.");
            String extension = filenameArray[filenameArray.length - 1];

            if (extension != null) {
                for (String type : IMAGE_TYPE) {
                    if (type.toLowerCase().equals(extension.toLowerCase())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void loadImageToImageView() {
        if (imageUri == null) {
            return;
        }

        Glide.with(this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageLoadingProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(pfPhoto);
    }

    protected void attemptSaveProfile() {
        // Hide the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        imageLoadingProgress.setVisibility(View.VISIBLE);
        try {
            StorageManager.uploadImage(imageUri, new StorageManager.OnImageUploadListener() {
                public void onUploaded(final String imageUrl) {
                    try {
                        updateProfile(imageUrl, profile.phoneNumber, profile.email, profile.facebookUrl, profile.twitterUrl);
                    }
                    catch (SecurityException ex) {
                        Toast.makeText(EditProfileActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                public void onFailed(Exception ex) {
                    Toast.makeText(EditProfileActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        finally {
            imageLoadingProgress.setVisibility(View.GONE);
        }
    }

    private void updateProfile(String imageUrl, String phoneNum, String email, String facebookUrl, String twitterUrl) {
        try {
            final Resources resources = this.getResources();
            final String backendPrefix = resources.getString(R.string.backend_prefix);
            String urlString;

            // call API and store profile info
            if(imageUrl != null) {
                urlString = String.format("%s/users/update?userName=%s&photoUrl=%s&phoneNumber=%s&email=%s&facebookUrl=%s&twitterUrl=%s",
                        backendPrefix, profile.userName, URLEncoder.encode(imageUrl, "UTF-8"), URLEncoder.encode(profile.phoneNumber, "UTF-8"),
                        URLEncoder.encode(profile.email, "UTF-8"), URLEncoder.encode(profile.facebookUrl, "UTF-8"),
                        URLEncoder.encode(profile.twitterUrl, "UTF-8"));
            }
            else{
                urlString = String.format("%s/users/update?userName=%s&phoneNumber=%s&email=%s&facebookUrl=%s&twitterUrl=%s",
                        backendPrefix, profile.userName, URLEncoder.encode(profile.phoneNumber, "UTF-8"),
                        URLEncoder.encode(profile.email, "UTF-8"), URLEncoder.encode(profile.facebookUrl, "UTF-8"),
                        URLEncoder.encode(profile.twitterUrl, "UTF-8"));
            }
            Request request = new JsonObjectRequest(Request.Method.PUT, urlString, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(EditProfileActivity.this, "Profile successfully updated", Toast.LENGTH_LONG).show();

                            // Go back to the settings fragment
                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);

                            intent.putExtra(MainActivity.FRAGMENT_TO_SELECT_KEY, SettingsFragment.Settings_Fragment_Tag);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errMsg = "Update profile failed";
                            if (error.networkResponse != null) {
                                errMsg = errMsg + "\n" + "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), errMsg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
            );

            RequestSingleton.getInstance(this).add(request);
        }
        catch (SecurityException ex) {
        }
        catch (Exception ex) {
            Toast.makeText(this, "Failed to save profile: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        };
    }

}
