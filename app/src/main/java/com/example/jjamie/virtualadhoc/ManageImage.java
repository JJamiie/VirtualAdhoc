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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public static File setUpPhotoFile(AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        File f = createImageFile(mAlbumStorageDirFactory);
//        System.out.println("Real Path----------------" + f.getAbsolutePath());
        return f;
    }

    public static File setUpPhotoFile(AlbumStorageDirFactory mAlbumStorageDirFactory,String filename) throws IOException{
        File f = createImageFile(mAlbumStorageDirFactory,filename);
        return f;
    }

    public static File createImageFile(AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir(mAlbumStorageDirFactory);
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }
    public static File createImageFile(AlbumStorageDirFactory mAlbumStorageDirFactory,String filename) throws IOException {
        // Create an image file name
        String imageFileName = filename;
        File albumF = getAlbumDir(mAlbumStorageDirFactory);
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }


    public static File getAlbumDir(AlbumStorageDirFactory mAlbumStorageDirFactory) {
//        System.out.println("getAlbumDir"+Environment.getExternalStorageState());
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
//            System.out.println("StorageDir--"+storageDir);
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
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

    public static void setPic(String mCurrentPhotoPath, ImageView mImageView) {

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
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
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


    public static void galleryAddPic(String mCurrentPhotoPath, Activity activity) {

        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        try {
            activity.getContentResolver().openInputStream(contentUri);
        } catch (IOException ex) {
            Log.d("GalleryAddPic", ex.toString());
        }
        activity.sendBroadcast(mediaScanIntent);
    }

    public static File[] getFile() {

        String path = Environment.getExternalStorageDirectory() + "/pictures/pegion";
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return files;
    }

    public static String getFileMetadata(int position) {

        String path = Environment.getExternalStorageDirectory() + "/pegion/metadata";
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        int length = (int) files[position].length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(files[position]);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }

    public static String readFromFileText(File file) {
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }


    public static File[] getFileMetadata() {

        String path = Environment.getExternalStorageDirectory() + "/pegion/metadata";
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        return files;
    }


    public static List<IPTCDataSet> createIPTCDataSet(String senderName, String description, String gps) {
        List<IPTCDataSet> iptcs = new ArrayList<IPTCDataSet>();
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CONTACT, senderName));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, description));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.SUB_LOCATION, gps));
        return iptcs;
    }

    public static List<IPTCDataSet> createIPTCDataSet(String senderName, String description) {
        List<IPTCDataSet> iptcs = new ArrayList<IPTCDataSet>();
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CONTACT, senderName));
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.KEY_WORDS, description));
        return iptcs;
    }

    public static Image changeFileToImage(String data, File file) {
//        int sequenceNumber = 1;
        byte[] img = new byte[(int) file.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(img, 0, img.length);
            buf.close();
            String[] d = data.split("---");
            Image image = new Image(d[0], file.getName(), d[1], d[2], img);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LengthIncorrectLengthException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeDataToFile(String senderName, String message, String location, String filename) {
        try {
            File pegionDirectory = new File(Environment.getExternalStorageDirectory() + "/pegion/metadata/");
            System.out.println(Environment.getExternalStorageDirectory());
            if (pegionDirectory != null) {
                if (!pegionDirectory.mkdirs()) {
                    if (!pegionDirectory.exists()) {
                        Log.d("CaptionActivity", "failed to create directory");
                        return;
                    }
                }
            }
            String name = filename.substring(0, filename.length() - 5) + ".txt";
            File file = new File(pegionDirectory.getPath(), name);
            FileOutputStream stream = new FileOutputStream(file);
            String data = senderName + "---" + message + "---" + location;
            try {
                stream.write(data.getBytes());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static boolean checkDuplicate(String filename){
        String path = Environment.getExternalStorageDirectory() + "/pictures/pegion";
        File dir = new File(path);
        File[] files = dir.listFiles();

        for(int i=0;i<files.length;i++){
            if(files[i].getName().equals(filename)){
                return true;
            }
        }
        if (files == null) return false;
        return false;
    }

}
