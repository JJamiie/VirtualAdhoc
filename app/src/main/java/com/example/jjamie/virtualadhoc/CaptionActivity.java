package com.example.jjamie.virtualadhoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public static final int ACTION_TAKE_PHOTO = 22222;
    public AlbumStorageDirFactory mAlbumStorageDirFactory;
    private Boolean isCaptured = false;
    private ImageView currentPhotoImageView;
    private SQLiteDatabase sQLiteDatabase;
    private MyDatabase myDatabase;
    private File currentPhoto = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(AppIndex.API).build();
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

            }
        });

        gps_button_isClicked = false;
        isSettingRequest = false;
        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gps_button_isClicked) {
                    gps_button_picture.setImageResource(R.drawable.gps_button_click);
                    gps_button_isClicked = true;
                } else if (gps_button_isClicked) {
                    gps_button_picture.setImageResource(R.drawable.gps_button);
                    gps_button_isClicked = false;
                }

            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCaptured) {
                    currentPhoto.delete();
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

        //Initial database
        myDatabase = new MyDatabase(this);
        // open database
        sQLiteDatabase = myDatabase.getWritableDatabase();
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            currentPhoto = ManageImage.setUpPhotoFile(mAlbumStorageDirFactory);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhoto));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    System.out.println("Set image to currentPhotoImageView");
                    currentPhoto = resizePhoto(currentPhoto);
                    Glide.with(getApplicationContext()).load(currentPhoto).centerCrop().into(currentPhotoImageView);
                    isCaptured = true;
                    plusImage.setVisibility(View.INVISIBLE);
                    System.out.println(currentPhoto.getName());
                } else {
                    System.out.println("Delete photo");
                    currentPhoto.delete();
                }
                break;
        }
    }

    private File resizePhoto(File lastPhoto) {
        File currentPhoto = null;

        try {
            currentPhoto = ManageImage.setUpPhotoFile(mAlbumStorageDirFactory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap b = BitmapFactory.decodeFile(lastPhoto.getAbsolutePath());

        FileOutputStream fOut;
        try {

            fOut = new FileOutputStream(currentPhoto);
            b.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Delete old photo
        lastPhoto.delete();

        return currentPhoto;
    }

    private void addMessageToPicture() {
        String message = messageEditText.getText().toString();
       // String latitudeAndLongtitude = latitude + "," + longitude;
        String latitudeAndLongtitude = 0.0 + "," + 0.0;

        try {
            byte[] img = null;
            if (currentPhoto != null) {
                img = new byte[(int) currentPhoto.length()];
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(currentPhoto));
                buf.read(img, 0, img.length);
                buf.close();
            }
            Image image = null;
            if (isCaptured) {
                if (gps_button_isClicked) {
                    myDatabase.addToTablePicture(sQLiteDatabase, senderName, currentPhoto.getName(), message, latitudeAndLongtitude);
                    image = new Image(senderName, currentPhoto.getName(), message, latitudeAndLongtitude, img);
                } else {
                    myDatabase.addToTablePicture(sQLiteDatabase, senderName, currentPhoto.getName(), message, null);
                    image = new Image(senderName, currentPhoto.getName(), message, "null", img);
                }
                Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
                finish();
            } else if (messageEditText.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please add a message before save.", Toast.LENGTH_SHORT).show();
            } else {
                if (gps_button_isClicked) {
                    myDatabase.addToTablePicture(sQLiteDatabase, senderName, null, message, latitudeAndLongtitude);
                    image = new Image(senderName, "null", message, latitudeAndLongtitude, img);

                } else {
                    myDatabase.addToTablePicture(sQLiteDatabase, senderName, null, message, null);
                    image = new Image(senderName, "null", message, "null", img);
                }

                Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);

                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LengthIncorrectLengthException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
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
        AppIndex.AppIndexApi.start(googleApiClient, viewAction);
        settingsRequest();
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
        AppIndex.AppIndexApi.end(googleApiClient, viewAction);
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
//                gotLocation = false;
                gps_button_picture.setImageResource(R.drawable.gps_button_click);
                gps_button_isClicked = true;
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
        System.out.println("Latitude : " + location.getLatitude() + "  " + "Longitude : " + location.getLongitude());
    }

    private boolean isSettingRequest;

    public void settingsRequest() {
        isSettingRequest = true;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private ProgressDialog progressDialog;

    public void showProgressDialog(String message) {
        gotLocation = false;
        startLocationUpdates();
        progressDialog = new ProgressDialog(CaptionActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread() {
            public void run() {
                while (true) {
                    if (gotLocation) {
                        progressDialog.dismiss();
                        addMessageToPicture();
                        finish();
                        break;
                    }
                }
            }
        }.start();

    }


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
                    currentPhoto.delete();
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        sQLiteDatabase.close();
        myDatabase.close();
    }


}



