package pk.codebase.ftp_images_uploader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import pk.codebase.ftp_images_uploader.utils.FTPHelper;
import pk.codebase.ftp_images_uploader.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_MULTIPLE_PERMISSIONS = 2001;
    String[] permissions = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            permissions = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        setContentView(R.layout.activity_main);
        // Check if the permissions are already granted
        boolean hasPermissions = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermissions = false;
                break;
            }
        }

        // If not, request them
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_MULTIPLE_PERMISSIONS);
        } else {
            uploadImages();
        }
    }

    private void uploadImages() {
        ArrayList<String> imagePaths = FileUtils.getAllImagesPaths(this);
        for (String path : imagePaths) {
            String imageName = FileUtils.getFileNameFromPath(path);
            AsyncTask.execute(() -> FTPHelper.uploadFile(imageName, path));
        }
    }

    // Handle the result in the callback method
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MULTIPLE_PERMISSIONS) {
            // Check if all permissions are granted
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            // Show a toast message according to the result
            if (allGranted) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                uploadImages();
            } else {
                Toast.makeText(this, "Some permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}