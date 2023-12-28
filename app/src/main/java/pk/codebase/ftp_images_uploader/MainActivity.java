package pk.codebase.ftp_images_uploader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.UUID;

import pk.codebase.ftp_images_uploader.utils.FTPHelper;
import pk.codebase.ftp_images_uploader.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    FTPClient ftpClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton = findViewById(R.id.my_button);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, UUID.randomUUID().toString(), null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            System.out.println(photo);
            Uri fileURI = getImageUri(getApplicationContext(), photo);
            File fileToUpload = new File(fileURI.getPath());
            String path = FileUtils.getPathFromUri(getApplicationContext(), getImageUri(getApplicationContext(), photo));
            System.out.println(path);
            System.out.println(fileToUpload.getName());
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    FTPHelper.connectToFTP();
                    String fileName = FileUtils.getFileNameFromUri(AppGlobals.getContext(), fileURI);
                    System.out.println("-----------the file name!");
                    System.out.println(fileName);
                    FTPHelper.uploadFile(fileName, path);
                }
            });

        }
    }
}