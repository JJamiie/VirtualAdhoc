package com.example.jjamie.virtualadhoc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button btnTakePhoto;
    Button btnBroadcast;
    ImageView imgTakenPhoto;
    Broadcaster broadcaster;
    private static final int CAM_REQUEST =1313;
    WifiManager wm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        broadcaster = new Broadcaster(wm);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageView1); //Show picture

        btnTakePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnBroadcast = (Button) findViewById(R.id.btnBroadcast);

        btnBroadcast.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastPicture();
            }
        });

    }

    static Uri imageUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST) {
            Bitmap scaled=null;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
            }catch (IOException ex){}

            if(scaled!=null ) {
                imgTakenPhoto.setImageBitmap(scaled);

            }
        }
    }

    public void dispatchTakePictureIntent(){

        String filename = Environment.getExternalStorageDirectory().getPath() + "/folder/testfile.jpg";
        imageUri = Uri.fromFile(new File(filename));
        // start default camera
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAM_REQUEST);
        }

    }


    public void broadcastPicture(){
        if(imageUri == null) return;
        try{
            InputStream iStream = getContentResolver().openInputStream(imageUri);
            byte[] data = getBytes(iStream);
            broadcaster.broadcast(data);
        }catch (IOException ex){

        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }




}
