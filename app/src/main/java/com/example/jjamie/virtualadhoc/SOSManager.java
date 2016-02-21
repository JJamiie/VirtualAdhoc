package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Administrator on 18/2/2559.
 */
public class SOSManager extends Thread implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Double latitude;
    private Double longitude;
    private boolean active = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public SOSManager(Activity activity) {
        googleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(AppIndex.API).build();
        googleApiClient.connect();
        latitude = 0.0;
        longitude = 0.0;
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (!active) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(3000);
                sendMessage();
                Thread.sleep(18000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private void sendMessage() {
        String message = "Help me!";
        if(latitude == 0.0 && longitude ==0.0){
            return;
        }
        String latitudeAndLongtitude = latitude + "," + longitude;
        try {
            Image image = new Image(TabActivity.senderName, "null", message, latitudeAndLongtitude, null);
            Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
        } catch (LengthIncorrectLengthException e) {
            e.printStackTrace();
        }

    }

    public void wake() {
        active = true;
        notifyAll();
    }

    public void sleep() {
        active = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        System.out.println("Latitude : " + location.getLatitude() + "  " + "Longitude : " + location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
