package com.example.jjamie.virtualadhoc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


public class ManageImage {

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String ALBUM_NAME = "Pigeon";

    public static File setUpPhotoFile(AlbumStorageDirFactory mAlbumStorageDirFactory) throws IOException {
        File f = createImageFile(mAlbumStorageDirFactory);
        return f;
    }

    public static File setUpPhotoFile(AlbumStorageDirFactory mAlbumStorageDirFactory, String filename) throws IOException {
        File f = createImageFile(mAlbumStorageDirFactory, filename);
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

    public static File createImageFile(AlbumStorageDirFactory mAlbumStorageDirFactory, String filename) throws IOException {
        // Create an image file name
        File albumF = getAlbumDir(mAlbumStorageDirFactory);
        File imageF = createTempFile(filename, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }


    public static File getAlbumDir(AlbumStorageDirFactory mAlbumStorageDirFactory) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(ALBUM_NAME);
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

        String path = Environment.getExternalStorageDirectory() + "/pictures/" + ManageImage.ALBUM_NAME;
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


    public static File isExist(String filename) {
        String path = Environment.getExternalStorageDirectory() + "/pictures/" + ManageImage.ALBUM_NAME;
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(filename)) {
                return files[i];
            }
        }
        return null;
    }

    public static File createTempFile(String prefix, String suffix, File directory)
            throws IOException {
        // Force a prefix null check first
        if (prefix.length() < 3) {
            throw new IllegalArgumentException("prefix must be at least 3 characters");
        }
        if (suffix == null) {
            suffix = ".tmp";
        }
        File tmpDirFile = directory;
        if (tmpDirFile == null) {
            String tmpDir = System.getProperty("java.io.tmpdir", ".");
            tmpDirFile = new File(tmpDir);
        }
        File result;
        do {
            result = new File(tmpDirFile, prefix + suffix);
        } while (!result.createNewFile());
        return result;
    }

}
