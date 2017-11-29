package edu.uw.yw239.wehike;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString(ContactsFragment.CONTACTS_INFO_KEY);

        TextView view = (TextView)findViewById(R.id.tv_conversation);
        view.setText(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    final static String BACK_TO_FRAGMENT_KEY = "back to fragment key";
    final static int BACK_TO_FRAGMENT_VALUE = 1;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                break;
        }
        return true;
    }

    private void goBack(){
        Intent intent = new Intent(ConversationActivity.this, MainActivity.class);

        intent.putExtra(BACK_TO_FRAGMENT_KEY,BACK_TO_FRAGMENT_VALUE);
        startActivity(intent);
    }
}
