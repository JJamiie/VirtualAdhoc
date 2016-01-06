package com.example.jjamie.virtualadhoc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import pixy.meta.iptc.IPTCApplicationTag;
import pixy.meta.iptc.IPTCDataSet;


public class ManageImage {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static File setUpPhotoFile(Activity activity,AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        File f = createImageFile(activity, mAlbumStorageDirFactory);

        return f;
    }

    public static File createImageFile(Activity activity,AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir(mAlbumStorageDirFactory);
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    public static File getAlbumDir(AlbumStorageDirFactory mAlbumStorageDirFactory) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Virtual Adoc", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v("Virtual Adhoc", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /* Photo album for this application */
    public static String getAlbumName() {
        return "Pegion";
    }

    public static void setPic(String mCurrentPhotoPath,ImageView mImageView) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }


    public static void galleryAddPic(String mCurrentPhotoPath,Activity activity) {

        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        try{
            activity.getContentResolver().openInputStream(contentUri);
        }catch (IOException ex){
            Log.d("GalleryAddPic",ex.toString());
        }
        activity.sendBroadcast(mediaScanIntent);
    }

    public static File[] getFile(){
        String path = "/storage/emulated/0/Pictures/Pegion";
        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return files;
    }

    public static List<IPTCDataSet> createIPTCDataSet(String senderName,String description,String gps) {
        List<IPTCDataSet> iptcs = new ArrayList<IPTCDataSet>();
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CONTACT, senderName));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, description));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.SUB_LOCATION, gps));
        return iptcs;
    }


}
