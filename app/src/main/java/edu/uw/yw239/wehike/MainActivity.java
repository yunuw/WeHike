package edu.uw.yw239.wehike;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.support.v4.app.Fragment;

import edu.uw.yw239.wehike.common.LocationManager;
import edu.uw.yw239.wehike.posts.CreatePostActivity;
import edu.uw.yw239.wehike.posts.PostsFragment;
import edu.uw.yw239.wehike.profile.EditProfileActivity;
import edu.uw.yw239.wehike.settings.SettingsFragment;
import edu.uw.yw239.wehike.trails.TrailsFragment;

/**
 * Created by Yun on 11/12/2017.
 */

public class MainActivity extends AppCompatActivity {
    private String selectedFragmentTag = null;

    public static final String FRAGMENT_TO_SELECT_KEY = "FRAGMENT_TO_SELECT_KEY";

    public Fragment trailsFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the location manager asap the app start
        LocationManager locManager = LocationManager.getInstance();

//        if (savedInstanceState != null){
//            trailsFragment = getSupportFragmentManager().getFragment(savedInstanceState,"trails");
//        }

        boolean openFragmentFromIntent = false;

        // Set up the back stack from CreatePostActivity
        Intent intent = getIntent();

        if(intent != null) {
            String val = intent.getStringExtra(FRAGMENT_TO_SELECT_KEY);
            if (val == null) {
                openFragmentFromIntent = false;
            }  else if (val.equals(TrailsFragment.Trails_Fragment_Tag)) {
                openFragmentFromIntent = true;
                showTrails(findViewById(R.id.trails_button));
            } else if (val.equals(PostsFragment.Posts_Fragment_Tag)) {
                openFragmentFromIntent = true;
                showPosts(findViewById(R.id.posts_button));
            } else if (val.equals(SettingsFragment.Settings_Fragment_Tag)) {
                openFragmentFromIntent = true;
                showSettings(findViewById(R.id.settings_button));
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

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, "trails",getSupportFragmentManager().findFragmentByTag(TrailsFragment.Trails_Fragment_Tag));
    }

    public void showTrails(View view) {
        if (this.selectedFragmentTag == TrailsFragment.Trails_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        TrailsFragment fragment = TrailsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, TrailsFragment.Trails_Fragment_Tag);
        transaction.commit();

        this.selectedFragmentTag = TrailsFragment.Trails_Fragment_Tag;
    }

    public void showPosts(View view) {
        if (this.selectedFragmentTag == PostsFragment.Posts_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        PostsFragment fragment = PostsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, PostsFragment.Posts_Fragment_Tag);
        transaction.commit();
        this.selectedFragmentTag = PostsFragment.Posts_Fragment_Tag;
    }

    public void showSettings(View view) {
        if (this.selectedFragmentTag == SettingsFragment.Settings_Fragment_Tag) {
            return;
        }

        setTabsBackgroundColor(view);

        SettingsFragment fragment = SettingsFragment.newInstance(null, null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, SettingsFragment.Settings_Fragment_Tag);
        transaction.commit();

        this.selectedFragmentTag = SettingsFragment.Settings_Fragment_Tag;
    }

    private void setTabsBackgroundColor(View view) {
        ImageButton showTrailsButton = (ImageButton)findViewById(R.id.trails_button);
        ImageButton showPostsButton = (ImageButton)findViewById(R.id.posts_button);
        ImageButton showSettingsButton = (ImageButton)findViewById(R.id.settings_button);

        int unselectedColor = getResources().getColor(R.color.colorTabUnselected);
        showTrailsButton.setBackgroundColor(unselectedColor);
        showPostsButton.setBackgroundColor(unselectedColor);
        showSettingsButton.setBackgroundColor(unselectedColor);

        int selectedColor = getResources().getColor(R.color.colorTabSelected);
        view.setBackgroundColor(selectedColor);
    }
}
