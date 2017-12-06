package edu.uw.yw239.wehike.trails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.RequestSingleton;

import static edu.uw.yw239.wehike.R.id.map;
import static edu.uw.yw239.wehike.common.MyApplication.getContext;

/**
 * Created by wangchen on 12/3/17.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private final static String TAG = "MapsActivity";
    public final static int BACK_TO_TRAILS_VALUE = 1;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private Location curLocation;

    private List<Trail> trailsList;
    HashMap<Marker,Trail> hashMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        trailsList = new ArrayList<Trail>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d(TAG, intent == null ? "Intent is null" : "Intent is not null");
        Log.d(TAG, bundle == null ? "Bundle is null" : "Bundle is not null");
        String q = bundle.getString("trailsSearchTerm");
        fetchTrailsMaps(q,47.6535,-122.3077);

        hashMap = new HashMap<>();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(this);


        CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(47.6535,-122.3077));
        mMap.moveCamera(cu);

    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Toast.makeText(this, "There are thousands of fat ducks here!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MapsActivity.this, TrailsDetailActivity.class);
        intent.putExtra("trails details",hashMap.get(marker));
        startActivity(intent);
        Log.v(TAG, "Trail Item clicked");
    }

    public void fetchTrailsMaps(String q, Double lat, Double lon){

        //contruct request url
        String http = "";
        if (q.equals("")){
            http = "https://trailapi-trailapi.p.mashape.com/?lat="+lat+"&limit=50&lon="+lon+"&radius=25";
        } else {
            http = "https://trailapi-trailapi.p.mashape.com/?q[city_cont]="+q+"&radius=25";
        }

        Log.v("what is the http?", http);

        Request mRequest = new JsonObjectRequest(Request.Method.GET, http, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, response.toString());

                        List<Trail> trails = Trail.parseTrailAPI(response);
                        Log.v(TAG, trails.toString());
//                        mAdapter = new NewsRecyclerViewAdapter(newsList);
//                        ((RecyclerView) recyclerView).setAdapter(mAdapter);
                        hashMap.clear();
                        trailsList.clear();
                        trailsList.addAll(trails);
                        for(int i = 0; i<trailsList.size(); i++){
                            //set Markers
                            Trail trail = trailsList.get(i);
                            LatLng latlng = new LatLng(trail.lat, trail.lon);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng)
                                    .title(trail.name)
                                    .snippet("Go Explore!")
                                    .icon(BitmapDescriptorFactory.defaultMarker(200)));
                            hashMap.put(marker,trail);
                        }

                        CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(trailsList.get(trailsList.size()/2).lat,trailsList.get(trailsList.size()/2).lon));
                        mMap.moveCamera(cu);


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Mashape-Key", "TzMlsrtBxvmshPb8Q6GChK9MIf0mp1YHXeAjsnVxykXTSr482e");
                headers.put("Accept", "text/plain");
                return headers;
            }
        };
        RequestSingleton.getInstance(getContext()).add(mRequest);
    }

    private void goBack(){
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);

        intent.putExtra(MainActivity.FRAGMENT_TO_SELECT_KEY, TrailsFragment.Trails_Fragment_Tag);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
