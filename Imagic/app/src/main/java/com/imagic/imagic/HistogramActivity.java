package com.imagic.imagic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class HistogramActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int CAMERA_TAKE_REQUEST = 200;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ImageView iv;
    File file;
    Uri imageUri;
    private Context context;
    private Activity activity;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this;
        activity = HistogramActivity.this;
        iv = (ImageView) findViewById(R.id.iv);
        imageBitmap = null;
    }

    public void generateHistogram(View v) {
        if(imageBitmap.equals(null)){
            Toast.makeText(activity, "No image selected/uploaded", Toast.LENGTH_SHORT).show();
        } else {
//            Log.v("COBA", "pixel(0,0)=" + Color.red(imageBitmap.getPixel(0,0)));
            Intent intent = new Intent(context, HistogramResultActivity.class);
            intent.putExtra("imageUri", imageUri);
            startActivity(intent);
        }
    }

    public void pickImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void takePicture(View v) {
        if(checkCameraExists()) {
//            if (permissionsToRequest.size() > 0) {
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
//                        ALL_PERMISSIONS_RESULT);
//            } else {
            launchCamera();
//            }
        } else {
            Toast.makeText(activity, "Camera not available.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void launchCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_TAKE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImage = Uri.parse("");
        switch(requestCode){
            case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                }
                break;
            case CAMERA_TAKE_REQUEST:
                if(resultCode == RESULT_OK){
                    selectedImage = Uri.parse(file.toURI().toString());
                }
                break;
        }

        try {
            if(!selectedImage.equals(Uri.parse(""))) {
                imageUri = selectedImage;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageBitmap = bitmap;
                iv.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
