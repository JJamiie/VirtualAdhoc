package com.example.jjamie.virtualadhoc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pixy.meta.Metadata;

public class CaptionActivity extends AppCompatActivity {

    private String currentPhotopath;
    private ImageView currentPhotoImageView;
    private EditText messageEditText;
    private Button editMessageButton;
    private ImageView gps_button;
    private File currentPhoto;
    private String senderName;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Boolean clicked;
    private String gpsLatLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        gpsLatLon = "";
        currentPhotoImageView = (ImageView) findViewById(R.id.imageForCaption);
        messageEditText = (EditText) findViewById(R.id.captionEditText);
        editMessageButton = (Button) findViewById(R.id.edit_message);
        gps_button = (ImageView) findViewById(R.id.gps_button);
        editMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessageToPicture();
                finish();
//                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
//                startActivity(intent);
            }
        });

        clicked = true;
        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showGPSDisabledAlertToUser();
                } else {
                    if (clicked) {
                        setLatAndLon(locationManager);
                        gps_button.setImageResource(R.drawable.gps_button_click);
                        clicked = false;
                    } else {
                        gpsLatLon = "";
                        gps_button.setImageResource(R.drawable.gps_button);
                        clicked = true;
                    }
                }
            }

        });

        Intent intent = getIntent();
        currentPhotopath = intent.getStringExtra("currentPhotoPath");
        currentPhoto = new File(currentPhotopath);
        senderName = intent.getStringExtra("senderName");
        Glide.with(getApplicationContext()).load(currentPhoto).centerCrop().placeholder(new ColorDrawable(0xFFc5c4c4)).into(currentPhotoImageView);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setLatAndLon(LocationManager locationManager) {
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getApplicationContext().getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);

            while (location == null) {
                ProgressDialog dialog = new ProgressDialog(CaptionActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }


            if (location != null) {
                gpsLatLon = "Latitude: " + location.getLatitude() + " Longtitude: " + location.getLongitude();
            }else{
                gpsLatLon="";
            }
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void addMessageToPicture() {
        try {
            Glide.get(getApplicationContext()).clearMemory();
            FileInputStream fin = new FileInputStream(currentPhoto.getAbsolutePath());
            FileOutputStream fout = new FileOutputStream("/storage/emulated/0/Pictures/Pegion/" + currentPhoto.getName() + "_0.jpg");
            String message = messageEditText.getText().toString();
            Metadata.insertIPTC(fin, fout, ManageImage.createIPTCDataSet(senderName, message, gpsLatLon), true);
            fin.close();
            fout.close();
            currentPhoto.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Caption Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jjamie.virtualadhoc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Caption Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jjamie.virtualadhoc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
