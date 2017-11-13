package edu.uw.yw239.wehike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {
    // TODO: replac with the permission that is actually needed
    private static final String[] RequiredPermissions = new String[] {
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALENDAR
    };

    // Use the same request code for permission since we don't have special handling for a specific permission
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if(isAllPermissionGranted()) {
            openMainActivityIfSignedIn();
        }
        else { //if we're missing permission.
            askForPermission();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void signIn(View view) {
        // TODO: call API, if sign in successful, open main Activity, otherwise popup dialog
        openMainActivity();
    }

    public void signUp(View view) {

    }

    public void findPassword(View view) {
        Toast.makeText(SignInActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
    }

    private void openMainActivityIfSignedIn() {
        // TODO: Check if already signed in
        boolean isSignedIn = false;

        if (isSignedIn) {
            openMainActivity();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isAllPermissionGranted()) {
                        openMainActivityIfSignedIn();
                    } else {
                        askForPermission();
                    }
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    finish();
                }
                return;
            }
        }
    }

    private boolean isAllPermissionGranted() {
        for (String permission : RequiredPermissions) {
            if (ContextCompat.checkSelfPermission(SignInActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void askForPermission() {
        for (String permission : RequiredPermissions) {
            if (ContextCompat.checkSelfPermission(SignInActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignInActivity.this, new String[] { permission }, REQUEST_CODE_PERMISSIONS);
                return;
            }
        }
    }
}
