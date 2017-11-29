package edu.uw.yw239.wehike;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Yun on 11/12/2017.
 */

public class MainActivity extends AppCompatActivity {
    private final String Trails_Fragment_Tag = "Trails_Fragment_Tag";
    private final String Posts_Fragment_Tag = "Posts_Fragment_Tag";
    private final String Contacts_Fragment_Tag = "Contacts_Fragment_Tag";
    private final String Settings_Fragment_Tag = "Settings_Fragment_Tag";

    private String selectedFragmentTag = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean openFragmentFromIntent = false;

        // Set up the back stack from ConversationActivity
        Intent intent = getIntent();

        if(intent != null) {
            int val = intent.getIntExtra(ConversationActivity.BACK_TO_FRAGMENT_KEY, 0);

            if (val == ConversationActivity.BACK_TO_FRAGMENT_VALUE) {
                openFragmentFromIntent = true;
                showContacts(findViewById(R.id.contacts_button));
            }
        }

        if(openFragmentFromIntent == false){
            ImageButton showTrailsButton = (ImageButton)findViewById(R.id.trails_button);
            showTrails(showTrailsButton);
        }
    }

    @Override
    public void onStart(){
        super.onStart();


    }

    public void showTrails(View view) {
        if (this.selectedFragmentTag == Trails_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        TrailsFragment fragment = TrailsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, Trails_Fragment_Tag);
        transaction.commit();

        this.selectedFragmentTag = Trails_Fragment_Tag;
    }

    public void showPosts(View view) {
        if (this.selectedFragmentTag == Posts_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        PostsFragment fragment = PostsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, Posts_Fragment_Tag);
        transaction.commit();
        this.selectedFragmentTag = Posts_Fragment_Tag;
    }

    public void showContacts(View view) {
        if (this.selectedFragmentTag == Contacts_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        ContactsFragment fragment = ContactsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, Contacts_Fragment_Tag);
        transaction.commit();

        this.selectedFragmentTag = Contacts_Fragment_Tag;
    }

    public void showSettings(View view) {
        if (this.selectedFragmentTag == Settings_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        SettingsFragment fragment = SettingsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, Settings_Fragment_Tag);
        transaction.commit();

        this.selectedFragmentTag = Settings_Fragment_Tag;
    }

    private void setTabsBackgroundColor(View view) {
        ImageButton showTrailsButton = (ImageButton)findViewById(R.id.trails_button);
        ImageButton showPostsButton = (ImageButton)findViewById(R.id.posts_button);
        ImageButton showContactsButton = (ImageButton)findViewById(R.id.contacts_button);
        ImageButton showSettingsButton = (ImageButton)findViewById(R.id.settings_button);

        int unselectedColor = getResources().getColor(R.color.colorTabUnselected);
        showTrailsButton.setBackgroundColor(unselectedColor);
        showPostsButton.setBackgroundColor(unselectedColor);
        showContactsButton.setBackgroundColor(unselectedColor);
        showSettingsButton.setBackgroundColor(unselectedColor);

        int selectedColor = getResources().getColor(R.color.colorTabSelected);
        view.setBackgroundColor(selectedColor);
    }
}
