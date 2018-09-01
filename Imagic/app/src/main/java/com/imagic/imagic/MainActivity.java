package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    // Image MIME type
    private static final String IMAGE_MIME_TYPE = "image/*";

    // Intent request codes
    private static final int SELECT_IMAGE_REQUEST_CODE = 0;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;

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
                intent.setType(MainActivity.IMAGE_MIME_TYPE);
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
                    startActivityForResult(intent, MainActivity.CAPTURE_IMAGE_REQUEST_CODE);
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
                    Uri uri = data.getData();
                }
                break;
            case CAPTURE_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                }
                break;
            default:
                break;
        }
    }
}
