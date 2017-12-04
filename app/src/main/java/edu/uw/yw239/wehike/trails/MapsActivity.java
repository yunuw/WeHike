package edu.uw.yw239.wehike.trails;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import edu.uw.yw239.wehike.R;

/**
 * Created by wangchen on 12/3/17.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private Location curLocation;

    public final static String BACK_TO_TRAILS_KEY = "back to trails key";
    public final static int BACK_TO_TRAILS_VALUE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        mapFragment.setRetainInstance(true);

//        if (mGoogleApiClient == null){
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//
//        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //Set a zoom control button
//        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //Set a marker
        LatLng fountainMarker = new LatLng(47.6535,-122.3077);
        Marker uwFountain = mMap.addMarker(new MarkerOptions().position(fountainMarker)
                .title("Marker in UW fountain")
                .snippet("Ducks here!")
                .icon(BitmapDescriptorFactory.defaultMarker(260)));

        uwFountain.showInfoWindow();
        mMap.setOnInfoWindowClickListener(this);

        PolylineOptions polyLine = new PolylineOptions()
                .add(new LatLng(47.653804, -122.307804))
                .add(new LatLng(47.653704, -122.307704))
                .add(new LatLng(47.653804, -122.307604))
                .add(new LatLng(47.653704, -122.307504))
                .add(new LatLng(47.653804, -122.307404))
                .width(25)
                .color(Color.WHITE);
        mMap.addPolyline(polyLine);
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Toast.makeText(this, "There are thousands of fat ducks here!", Toast.LENGTH_SHORT).show();
    }

}
