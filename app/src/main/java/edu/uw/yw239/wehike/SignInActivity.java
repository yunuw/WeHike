package edu.uw.yw239.wehike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // TODO: Check permission
        // TODO: Check if already signed in
        openMainActivity();
    }

    protected void onResume() {
        super.onResume();
        // TODO: Samething as on start
        openMainActivity();
    }


    public void signIn() {
        // TODO: call API, if sign in successful, open main Activity, otherwise popup dialog
        openMainActivity();
    }

    public void signUp() {

    }

    private void openMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
