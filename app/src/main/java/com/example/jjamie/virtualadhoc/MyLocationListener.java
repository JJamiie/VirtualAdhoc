package com.example.jjamie.virtualadhoc;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        double latitude =  location.getLatitude();
        double longitude = location.getLongitude();
        Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
