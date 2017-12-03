package edu.uw.yw239.wehike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import edu.uw.yw239.wehike.utils.AccountInfo;

public class SignInActivity extends AppCompatActivity {
    // TODO: replac with the permission that is actually needed
    private static final String[] RequiredPermissions = new String[] {
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    };

    // Use the same request code for permission since we don't have special handling for a specific permission
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private String backendPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        backendPrefix = this.getResources().getString(R.string.backend_prefix);

        if(isAllPermissionGranted()) {
            openMainActivityIfSignedIn();
        }
        else { //if we're missing permission.
            askForPermission();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void signIn(View view) {
        EditText signinUserName = (EditText) this.findViewById(R.id.input_user_name);
        EditText signinPassword = (EditText) this.findViewById(R.id.input_pwd);
        final String userName = signinUserName.getText().toString();
        final String password = signinPassword.getText().toString();

        String urlString = String.format("%s/users/login?userName=%s&password=%s", backendPrefix, userName, password);
        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");

                        if (success) {
                            String authToken = response.getString("authToken");
                            saveCredential(userName, authToken);

                            openMainActivity();
                        } else {
                            String msg = response.getString("message");
                            Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errMsg = "Sign in failed!";
                    if (error.networkResponse != null) {
                        errMsg = "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                    }
                    Toast.makeText(SignInActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
            }
        );

        RequestSingleton.getInstance(this).add(request);
    }

    public void openSignUpDialog(View view) {
        SignUpDiaglog dialog = SignUpDiaglog.newInstance();
        dialog.show(getSupportFragmentManager(), null);
    }

    public void findPassword(View view) {
        Toast.makeText(SignInActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
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

    private void openMainActivityIfSignedIn() {
        try {
            final String userName = AccountInfo.getCurrentUserName();
            final String authToken = AccountInfo.getAuthToken();

            if (userName == null || authToken == null) {
                return;
            }

            String urlString = String.format("%s/users/verify", backendPrefix);
            Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    openMainActivity();
                                } else {
                                    String msg = response.getString("message");
                                    Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SignInActivity.this,
                                    "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("AuthorizationHeader", authToken);
                    return mHeaders;
                }
            };

            RequestSingleton.getInstance(this).add(request);
        } catch (Exception ioe) {
            Toast.makeText(this, ioe.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveCredential(String userName, String authToken) {
        try {
            FileOutputStream fos = openFileOutput(AccountInfo.CREDENTIAL_FILE_NAME, MODE_PRIVATE);

            PrintWriter out = new PrintWriter(fos);
            out.println(userName);
            out.println(authToken);
            out.close();
        }catch (IOException ioe){
            Toast.makeText(this, ioe.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
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
