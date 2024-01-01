package pk.codebase.ftp_images_uploader.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {

    public static String getPathFromUri(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            // Handle error
            Log.e("FileUtils", "Cursor is null for URI: " + uri);
        }

        return filePath;
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        String fileName = null;

        // Check if the URI has a "file" scheme
        if ("file".equals(uri.getScheme())) {
            fileName = uri.getLastPathSegment();
        } else if ("content".equals(uri.getScheme())) {
            // If the URI has a "content" scheme, query the MediaStore for the file name
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = null;

            try {
                cursor = contentResolver.query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return fileName;
    }

    public static String getFileNameFromPath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    public static ArrayList<String> getAllImagesPaths(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();

        // Set up the projection for the query
        String[] projection = {MediaStore.Images.Media.DATA};

        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // Create the URI for the external storage
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Perform the query
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        // Check if the cursor is not null and move to the first row
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            do {
                // Get the image path from the cursor
                String imagePath = cursor.getString(columnIndex);

                // Add the image path to the list
                imagePaths.add(imagePath);
            } while (cursor.moveToNext());

            // Close the cursor
            cursor.close();
        }

        return imagePaths;
    }

}
