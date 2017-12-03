package edu.uw.yw239.wehike.posts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.LocationManager;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.common.StorageManager;

/**
 * Created by Nan on 11/28/17.
 */

public class CreatePostActivity extends AppCompatActivity {
    private ImageView pickedImage;
    private EditText postDesc;
    private ProgressBar imageLoadingProgress;
    private Uri imageUri;

    // todo: change the parameter
    private static final int MAX_IMAGE_SIZE = 10485760;   //10 Mb
    private static final String [] IMAGE_TYPE = new String[]{"jpg", "png", "jpeg", "bmp", "jp2", "psd", "tif", "gif"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Hide the tittle
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pickedImage = (ImageView) findViewById(R.id.iv_selected_image);
        postDesc = (EditText) findViewById(R.id.et_post_desc);
        imageLoadingProgress = (ProgressBar) findViewById(R.id.pb_image_loading);

        pickedImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

        // Set up the hint status for post description
        //postDesc.setHint(getResources().getString(R.string.post_desc_hint));

        postDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    postDesc.setHint(getResources().getString(R.string.post_desc_hint));
                else
                    postDesc.setHint("");
            }
        });


    }

    public void onResume(){
        postDesc.setHint(getResources().getString(R.string.post_desc_hint));
        super.onResume();
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

    // Check whether the uri is a image
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
                .into(pickedImage);
    }

    protected void attemptCreatePost() {
        // Reset errors.
        postDesc.setError(null);

        final String description = postDesc.getText().toString().trim();

        View focusView = null;
        boolean cancel = false;

        if (imageUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.warning_empty_image);
            builder.setPositiveButton(R.string.dialog_ok_button, null);
            builder.show();
            focusView = pickedImage;
            cancel = true;
        }
        if (description == null || description.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.warning_empty_description);
            builder.setPositiveButton(R.string.dialog_ok_button, null);
            builder.show();
            focusView = postDesc;
            cancel = true;
        }

        if (!cancel) {
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
                            Location location = LocationManager.getInstance().getLocation();
                            if (location != null) {
                                createPost(imageUrl, description, location);
                            } else {
                                Toast.makeText(CreatePostActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (SecurityException ex) {
                            Toast.makeText(CreatePostActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onFailed(Exception ex) {
                        Toast.makeText(CreatePostActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally {
                imageLoadingProgress.setVisibility(View.GONE);
            }
        } else if (focusView != null) {
            focusView.requestFocus();
        }
    }

    private void createPost(String imageUrl, String description, Location location) {
        try {
            String userName = AccountInfo.getCurrentUserName();
            final Resources resources = this.getResources();
            final String backendPrefix = resources.getString(R.string.backend_prefix);


            // call API and register new user
            String urlString = String.format("%s/posts/create?userName=%s&imageUrl=%s&description=%s&longitude=%.3f&latitude=%.3f",
                    backendPrefix, userName, URLEncoder.encode(imageUrl, "UTF-8"), URLEncoder.encode(description, "UTF-8"),
                    location.getLongitude(), location.getLatitude());
            Request request = new JsonObjectRequest(Request.Method.POST, urlString, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)  {
                            try {
                                boolean success = response.getBoolean("success");

                                if (success) {
                                    Toast.makeText(CreatePostActivity.this, "Post created", Toast.LENGTH_LONG).show();
                                    setResult(RESULT_OK);
                                    CreatePostActivity.this.finish();
                                } else {
                                    String msg = response.getString("message");
                                    Toast.makeText(CreatePostActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errMsg = resources.getString(R.string.error_create_post_fail);
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
            Toast.makeText(this, "Failed to create post: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        };
    }

    private void goBack(){
        Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                break;

            case R.id.post:
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    attemptCreatePost();
                } else {
                    Snackbar
                            .make(findViewById(android.R.id.content), R.string.internet_connection_failed, Snackbar.LENGTH_LONG)
                            .show();
                }

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
