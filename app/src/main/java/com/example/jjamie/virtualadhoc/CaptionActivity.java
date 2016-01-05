package com.example.jjamie.virtualadhoc;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
    private File currentPhoto;
    private String senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);

        currentPhotoImageView = (ImageView) findViewById(R.id.imageForCaption);
        messageEditText = (EditText) findViewById(R.id.captionEditText);
        editMessageButton = (Button) findViewById(R.id.edit_message);
        editMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessageToPicture();
                Intent intent = new Intent(getApplicationContext(),TabActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        currentPhotopath = intent.getStringExtra("currentPhotoPath");
        currentPhoto = new File(currentPhotopath);
        senderName = intent.getStringExtra("senderName");
        Glide.with(getApplicationContext()).load(currentPhoto).centerCrop().placeholder(new ColorDrawable(0xFFc5c4c4)).into(currentPhotoImageView);

    }

    private void addMessageToPicture() {
        try {
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


}
