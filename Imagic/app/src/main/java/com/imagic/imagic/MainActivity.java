package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Intent request codes
    private static final int SELECT_IMAGE_REQUEST_CODE = 0;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;

    // Selected or captured image URI
    private static Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button captureImageButton = findViewById(R.id.captureImageButton);

        selectImageButton.setOnClickListener(getSelectImageButtonOnClickListener());
        captureImageButton.setOnClickListener(getCaptureImageButtonOnClickListener());
    }

    // Select image button on click listener
    private View.OnClickListener getSelectImageButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(Image.MIME_TYPE);
                startActivityForResult(intent, MainActivity.SELECT_IMAGE_REQUEST_CODE);
            }
        };
    }

    // Capture image button on click listener
    private View.OnClickListener getCaptureImageButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        File image = Image.createImage(MainActivity.this);
                        MainActivity.imageURI = Image.getFileProviderURI(MainActivity.this, image);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, MainActivity.imageURI);
                        startActivityForResult(intent, MainActivity.CAPTURE_IMAGE_REQUEST_CODE);
                    }
                    catch(Exception e) {
                        Toast.makeText(MainActivity.this, Image.CREATE_IMAGE_ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    // Select or capture image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SELECT_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    MainActivity.imageURI = data.getData();
                }
                break;
            case CAPTURE_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Log.d("ImageURI", MainActivity.imageURI.toString());
                }
                break;
            default:
                break;
        }
    }
}
