package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class HistogramResultActivity extends AppCompatActivity {
    private Bitmap imageBitmap;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_result);

        Intent intent = getIntent();
        imageUri = (Uri) intent.getParcelableExtra("imageUri");
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("COBA", "pixel(0,0)=" + Color.red(imageBitmap.getPixel(0,0)));
    }
}
