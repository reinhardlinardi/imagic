package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Intent request codes
    private enum RequestCode {
        SELECT_IMAGE(0), CAPTURE_IMAGE(1);

        public int value;
        RequestCode(int value) { this.value = value; }
    }

    // Selected or captured image URI
    private static Uri externalSharedURI;

    // Internal shared cached image data URI
    private static Uri imageDataURI;

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
                startActivityForResult(intent, MainActivity.RequestCode.SELECT_IMAGE.value);
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
                        @SuppressLint("SimpleDateFormat") String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File externalSharedImageFile = File.createTempFile(filename, ".jpg", getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                        MainActivity.externalSharedURI = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".provider", externalSharedImageFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, MainActivity.externalSharedURI);
                        startActivityForResult(intent, MainActivity.RequestCode.CAPTURE_IMAGE.value);
                    }
                    catch(Exception e) {
                        Log.e("Imagic", "Exception", e);
                    }
                }
            }
        };
    }

    // Select or capture image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == MainActivity.RequestCode.SELECT_IMAGE.value) MainActivity.externalSharedURI = data.getData();

            try {
                File imageDataFile = File.createTempFile("cache", ".imagic", getApplicationContext().getCacheDir());
                if(imageDataFile.exists() && imageDataFile.isFile()) imageDataFile.delete();

                MainActivity.imageDataURI = Uri.fromFile(imageDataFile);
                Image image = new Image(this, MainActivity.externalSharedURI);
                String json = image.jsonSerialize();

                BufferedWriter writer = new BufferedWriter(new FileWriter(imageDataFile));
                writer.write(json);
                writer.close();

                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra("imageData", MainActivity.imageDataURI.toString());
                startActivity(intent);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }
}
