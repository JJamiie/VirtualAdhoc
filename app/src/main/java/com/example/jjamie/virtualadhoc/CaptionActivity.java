package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.IOException;

public class CaptionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private LocationRequest locationRequest;
    private EditText messageEditText;
    private Button editMessageButton;
    private Button gps_button;
    private Button camera_button;
    private ImageView plusImage;
    private ImageView gps_button_picture;
    private String senderName;
    private Boolean gps_button_isClicked;
    private Boolean gotLocation = true;
    private GoogleApiClient googleApiClient;
    private Double latitude;
    private Double longitude;
    protected static final int REQUEST_CHECK_SETTINGS = 111111;
    public static final int ACTION_TAKE_PHOTO = 22222;
    public AlbumStorageDirFactory mAlbumStorageDirFactory;
    private Boolean isCaptured = false;
    private ImageView currentPhotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        camera_button = (Button) findViewById(R.id.camera_button);
        currentPhotoImageView = (ImageView) findViewById(R.id.imageForCaption);
        messageEditText = (EditText) findViewById(R.id.captionEditText);
        editMessageButton = (Button) findViewById(R.id.edit_message);
        gps_button = (Button) findViewById(R.id.gps_button);
        gps_button_picture = (ImageView) findViewById(R.id.gps_button_image);
        plusImage = (ImageView) findViewById(R.id.plusImage);
        editMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps_button_isClicked) {
                    showProgressDialog("Loading...");
                    System.out.println("Set location: " + latitude + "," + longitude);
                } else {
                    addMessageToPicture();
                }
                finish();

            }
        });

        gps_button_isClicked = false;
        isSettingRequest = false;
        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean gps_enabled;
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gps_enabled) {
                    settingsrequest();
                } else {
                    if (!gps_button_isClicked) {
                        if (!isSettingRequest) {
                            settingsrequest();
                        }
                        gps_button_picture.setImageResource(R.drawable.gps_button_click);
                        gps_button_isClicked = true;
                        gotLocation = false;
                        startLocationUpdates();
                    } else if (gps_button_isClicked) {
                        gps_button_picture.setImageResource(R.drawable.gps_button);
                        gps_button_isClicked = false;
                    }

                }
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCaptured) {
                    ManageImage.getFile()[0].delete();
                }
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
            }
        });

        Intent intent = getIntent();
        senderName = intent.getStringExtra("senderName");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File currentPhoto = ManageImage.setUpPhotoFile(this, mAlbumStorageDirFactory);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhoto));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case ACTION_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    System.out.println("Set image to currentPhotoImageView");
                    File currentPhoto = ManageImage.getFile()[0];
                    Glide.with(getApplicationContext()).load(currentPhoto).centerCrop().into(currentPhotoImageView);
                    isCaptured = true;
                    plusImage.setVisibility(View.INVISIBLE);
                    System.out.println(currentPhoto.getName());
                } else {
                    System.out.println("Delete photo");
                    ManageImage.getFile()[0].delete();
                }
                break;
        }
    }

    private void addMessageToPicture() {
            File currentPhoto = new File(ManageImage.getFile()[0].getAbsolutePath());
            String message = messageEditText.getText().toString();
            String latitudeAndLongtitude = latitude + "," + longitude;
            if (gps_button_isClicked) {
                ManageImage.writeDataToFile(senderName,message,latitudeAndLongtitude, currentPhoto.getName());
            } else {
                ManageImage.writeDataToFile(senderName,message,"null",currentPhoto.getName());

            }
    }


    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                gps_button_picture.setImageResource(R.drawable.gps_button_click);
                gps_button_isClicked = true;
                gotLocation = false;
            }
        });
    }


    @Override
    public void onConnectionSuspended(int i) {
        // Do something when Google API Client connection was suspended
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do something when Google API Client connection failed
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        gotLocation = true;
        System.out.println("GotLocation: " + gotLocation);
        System.out.println("Latitude : " + location.getLatitude() + "  " + "Longitude : " + location.getLongitude());
    }

    private boolean isSettingRequest;

    public void settingsrequest() {
        isSettingRequest = true;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(CaptionActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    private ProgressDialog progressDialog;

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(CaptionActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread() {
            public void run() {
                while (true) {
                    if (gotLocation) {
                        progressDialog.dismiss();
//                        handler.sendEmptyMessage(0);
                        addMessageToPicture();
                        break;
                    }
                }
            }
        }.start();

    }

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            progressDialog.dismiss();
//        }
//    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                if (isCaptured) {
                    ManageImage.getFile()[0].delete();
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}



