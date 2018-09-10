package com.imagic.imagic;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Intent request codes
    private enum RequestCode {
        SELECT_IMAGE(0), CAPTURE_IMAGE(1);

        public int code;
        RequestCode(int code) { this.code = code; }
    }

    // Selected or captured image URI
    private Uri imageURI;

    // Cached image data URI
    private Uri cachedImageDataURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cachedImageDataURI = Cache.NO_CACHE_URI;

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
                startActivityForResult(intent, RequestCode.SELECT_IMAGE.code);
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
                        imageURI = Cache.create(MainActivity.this, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "jpg", true);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                        startActivityForResult(intent, RequestCode.CAPTURE_IMAGE.code);
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
            Cache.deleteOldCache(cachedImageDataURI);
            if(requestCode == RequestCode.SELECT_IMAGE.code) imageURI = data.getData();

            try {
                cachedImageDataURI = Cache.create(this, "imagic", "cache", false);
                Image image = new Image(this, imageURI);
                Cache.write(cachedImageDataURI, JSONSerializer.serialize(image));

                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra(Cache.INTENT_BUNDLE_NAME, cachedImageDataURI.toString());
                startActivity(intent);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }
}
