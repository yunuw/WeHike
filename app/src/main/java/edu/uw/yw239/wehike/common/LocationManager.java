package edu.uw.yw239.wehike.common;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Yun on 12/3/2017.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static LocationManager manager;
    private Location location;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private GoogleApiClient googleApiClient;

    //public static final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MyApplication.getContext());

    private LocationManager() {
        initialize();
    }

    public static LocationManager getInstance() {
        if (manager == null) {
            manager = new LocationManager();
        }

        return manager;
    }

    public Location getLocation() {
        return location;
    }

    private void initialize() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        googleApiClient = new GoogleApiClient.Builder(MyApplication.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        try {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            Toast.makeText(MyApplication.getContext(), "Google Api Client connection succeeded!", Toast.LENGTH_LONG).show();
        } catch (SecurityException ex) {
            Toast.makeText(MyApplication.getContext(), "Failed to request location " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MyApplication.getContext(), "Google Api Client connection suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MyApplication.getContext(), "Google Api Client connection failed!", Toast.LENGTH_LONG).show();
    }
}
