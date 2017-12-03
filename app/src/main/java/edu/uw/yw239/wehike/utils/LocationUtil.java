package edu.uw.yw239.wehike.utils;

import android.app.Service;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import edu.uw.yw239.wehike.MyApplication;

/**
 * Created by Yun on 12/3/2017.
 */

public class LocationUtil {
      public static final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MyApplication.getContext());
}
