package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HistogramResultActivity extends AppCompatActivity {
    private Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_result);

        Intent intent = getIntent();
        imageBitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        Log.v("COBA", "pixel(0,0)=" + Color.red(imageBitmap.getPixel(0,0)));
    }
}
