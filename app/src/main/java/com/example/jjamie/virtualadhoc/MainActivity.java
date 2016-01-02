//package com.example.jjamie.virtualadhoc;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    public static final int ACTION_TAKE_PHOTO_B = 1;
//    public static final int ACTION_RECEIVED_PHOTO = 2;
//    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
//    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
//
//    public ImageView mImageView;
//    public ImageView mImageViewReceive;
//    public Bitmap mImageBitmap;
//
//    public WifiManager mWifi;
//    private String mCurrentPhotoPath;
//
//    public void setmCurrentPhotoReceivePath(String mCurrentPhotoReceivePath) {
//        this.mCurrentPhotoReceivePath = mCurrentPhotoReceivePath;
//    }
//
//    private String mCurrentPhotoReceivePath;
//    private Image imageToSent;
//    int sequenceNumber;
//    String senderName;
//
//    public static AlbumStorageDirFactory mAlbumStorageDirFactory;
//    public static ContentResolver contentResolver;
//
//    public Broadcaster broadcaster;
//    public Listener listener;
//    public static MainActivity th;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        imageToSent =null;
//        senderName = "Jay";
//        sequenceNumber = 0;
//        th=this;
//
//        contentResolver = getContentResolver();
//        mWifi = (WifiManager) getSystemService(WIFI_SERVICE);
//
//        broadcaster = new Broadcaster(mWifi);
//        mImageView = (ImageView) findViewById(R.id.imageView1);
//        mImageViewReceive = (ImageView) findViewById(R.id.imageViewReceive);
//        listener = new Listener(mWifi,broadcaster);
//        listener.start();
//
//
//        mImageBitmap = null;
//
//        Button picBtn = (Button) findViewById(R.id.btnIntend);
//        setBtnListenerOrDisable(picBtn, mTakePicOnClickListener, MediaStore.ACTION_IMAGE_CAPTURE);
//
//        Button broadcastBtn = (Button) findViewById(R.id.broadcast);
//        setBtnListener(broadcastBtn, brodcastOnClickListener);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
//            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
//        } else {
//            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
//        }
//
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//
//    }
//
//    Button.OnClickListener brodcastOnClickListener =
//            new Button.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    broadcaster.broadcast(imageToSent);
//
//                }
//            };
//
//    Button.OnClickListener mTakePicOnClickListener =
//            new Button.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
//                }
//            };
//
//
//    private void dispatchTakePictureIntent(int actionCode) {
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        switch(actionCode) {
//            case ACTION_TAKE_PHOTO_B:
//                File f = null;
//
//                try {
//                    f = ManageImage.setUpPhotoFile();
//                    mCurrentPhotoPath = f.getAbsolutePath();
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    f = null;
//                    mCurrentPhotoPath = null;
//                }
//                break;
//
//            default:
//                break;
//        } // switch
//
//        startActivityForResult(takePictureIntent, actionCode);
//    }
//
//
//    private void handleCameraPhoto() {
//
//        if (mCurrentPhotoPath != null) {
//            ManageImage.setPic(mCurrentPhotoPath, mImageView);
//            Intent mediaScanIntent = ManageImage.galleryAddPic(mCurrentPhotoPath);
//            this.sendBroadcast(mediaScanIntent);
//            setDataToSent(mCurrentPhotoPath);
//            mCurrentPhotoPath = null;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case ACTION_TAKE_PHOTO_B: {
//                if (resultCode == RESULT_OK) {
//                    handleCameraPhoto();
//                }
//                break;
//            } // ACTION_TAKE_PHOTO_B
//            case ACTION_RECEIVED_PHOTO: {
//                if (resultCode == RESULT_OK){
//                    setRecievedPic();
//                }
//            }
//        } // switch
//    }
//
//    // Some lifecycle callbacks so that the image can survive orientation change
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
//        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
//        mImageView.setImageBitmap(mImageBitmap);
//        mImageView.setVisibility(
//                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
//                        ImageView.VISIBLE : ImageView.INVISIBLE
//        );
//    }
//
//    /**
//     * Indicates whether the specified action can be used as an intent. This
//     * method queries the package manager for installed packages that can
//     * respond to an intent with the specified action. If no suitable package is
//     * found, this method returns false.
//     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
//     *
//     * @param context The application's environment.
//     * @param action The Intent action to check for availability.
//     *
//     * @return True if an Intent with the specified action can be sent and
//     *         responded to, false otherwise.
//     */
//    public static boolean isIntentAvailable(Context context, String action) {
//        final PackageManager packageManager = context.getPackageManager();
//        final Intent intent = new Intent(action);
//        List<ResolveInfo> list =
//                packageManager.queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//        return list.size() > 0;
//    }
//
//    private void setBtnListenerOrDisable(Button btn, Button.OnClickListener onClickListener, String intentName) {
//        if (isIntentAvailable(this, intentName)) {
//            btn.setOnClickListener(onClickListener);
//        } else {
//            btn.setText(getText(R.string.cannot).toString() + " " + btn.getText());
//            btn.setClickable(false);
//        }
//    }
//
//    private void setBtnListener(Button btn, Button.OnClickListener onClickListener) {
//        btn.setOnClickListener(onClickListener);
//    }
//
////    public byte[] getBytes(InputStream inputStream) throws IOException {
////        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
////        int bufferSize = 1024;
////        byte[] buffer = new byte[bufferSize];
////
////        int len = 0;
////        while ((len = inputStream.read(buffer)) != -1) {
////            byteBuffer.write(buffer, 0, len);
////        }
////        return byteBuffer.toByteArray();
////    }
//
//    public void setRecievedPic(){
//        ManageImage.setPic(mCurrentPhotoReceivePath, mImageViewReceive);
//        Intent mediaScanIntent = ManageImage.galleryAddPic(mCurrentPhotoReceivePath);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    public void setDataToSent(String path){
////        int DESIREDWIDTH = 240;
////        int DESIREDHEIGHT = 320;
////        Bitmap scaledBitmap = null;
////
////        Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, "FIT");
////
////        if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
////            // Part 2: Scale image
////            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, "FIT");
////        } else {
////            unscaledBitmap.recycle();
////        }
////
////        ByteArrayOutputStream stream = new ByteArrayOutputStream();
////        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
//        File file = new File(path);
//        int size = (int) file.length();
//        byte[] img = new byte[size];
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//            buf.read(img, 0, img.length);
//            buf.close();
//            imageToSent = new Image(senderName, sequenceNumber, img);
//            sequenceNumber++;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SenderNameIncorrectLengthException senderName){
//
//        }
//    }
//
//}