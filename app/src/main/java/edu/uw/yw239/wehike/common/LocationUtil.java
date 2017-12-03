package edu.uw.yw239.wehike.common;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Yun on 12/3/2017.
 */

public class LocationUtil {
      public static final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MyApplication.getContext());
}
