package com.example.jjamie.virtualadhoc;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        currentPhotoImageView = (ImageView) findViewById(R.id.imageForCaption);
        messageEditText = (EditText) findViewById(R.id.captionEditText);
        editMessageButton = (Button) findViewById(R.id.edit_message);
        gps_button = (ImageView) findViewById(R.id.gps_button);
        editMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessageToPicture();
                finish();
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                startActivity(intent);
            }
        });

        clicked = true;
        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked){
                    gps_button.setImageResource(R.drawable.gps_button_click);
                    clicked = false;
                }else{
                    gps_button.setImageResource(R.drawable.gps_button);
                    clicked = true;
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

    private void addMessageToPicture() {
        try {
            Glide.get(getApplicationContext()).clearMemory();
            FileInputStream fin = new FileInputStream(currentPhoto.getAbsolutePath());
            FileOutputStream fout = new FileOutputStream("/storage/emulated/0/Pictures/Pegion/" + currentPhoto.getName() + "_0.jpg");
            String message = messageEditText.getText().toString();
            Metadata.insertIPTC(fin, fout, ManageImage.createIPTCDataSet(senderName, message), true);
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
