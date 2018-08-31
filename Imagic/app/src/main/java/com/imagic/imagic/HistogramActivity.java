package com.imagic.imagic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class HistogramActivity extends AppCompatActivity {

    // Constants
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int CAMERA_TAKE_REQUEST = 200;

    // Activity and context
    private Context context;
    private Activity activity;

    // Image
    private File file;
    private Uri imageUri;
    private ImageView imageView;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            context = this;
            activity = HistogramActivity.this;
            imageView = findViewById(R.id.imageView);
            imageBitmap = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImage = null;

        switch(requestCode) {
            case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                }
                break;
            case CAMERA_TAKE_REQUEST:
                if(resultCode == RESULT_OK) {
                    selectedImage = Uri.parse(file.toURI().toString());
                }
                break;
            default:
                break;
        }

        try {
            if(selectedImage != null) {
                imageUri = selectedImage;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageBitmap = bitmap;
                imageView.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if camera feature is available
    private boolean isCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    // Launch camera
    private void launchCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_TAKE_REQUEST);
    }

    // Generate histogram
    public void generateHistogram(View v) {
        if(imageBitmap == null){
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, HistogramResultActivity.class);
            intent.putExtra("imageUri", imageUri);
            startActivity(intent);
        }
    }

    // Select image
    public void pickImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Take picture from camera
    public void takePicture(View v) {
        if(isCameraExists()) launchCamera();
        else {
            Toast.makeText(activity, "Camera not available.", Toast.LENGTH_SHORT).show();
        }
    }
}
