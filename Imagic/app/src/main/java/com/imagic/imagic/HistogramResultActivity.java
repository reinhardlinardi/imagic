package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;

import java.io.IOException;

public class HistogramResultActivity extends AppCompatActivity {
    private static final int COLOR_VALUE_BOUNDARY = 256;

    private Bitmap imageBitmap;
    private Uri imageUri;
    private int imageWidth;
    private int imageHeight;

    private int[] redValuesCount;
    private int[] greenValuesCount;
    private int[] blueValuesCount;
    private int[] grayscaleValuesCount;

    private BarChart redChart;
    private BarChart greenChart;
    private BarChart blueChart;
    private BarChart grayscaleChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_result);

        initializeColorValuesCount();
        Intent intent = getIntent();
        imageUri = (Uri) intent.getParcelableExtra("imageUri");
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageHeight = imageBitmap.getHeight();
        imageWidth = imageBitmap.getWidth();
        countColorValues();
//        Log.v("COBA", "pixel(0,0)=" + Color.red(imageBitmap.getPixel(0,0)));
    }

    private void initializeColorValuesCount() {
        redValuesCount = new int[COLOR_VALUE_BOUNDARY];
        greenValuesCount = new int[COLOR_VALUE_BOUNDARY];
        blueValuesCount = new int[COLOR_VALUE_BOUNDARY];
        grayscaleValuesCount = new int[COLOR_VALUE_BOUNDARY];

        for(int it = 0; it < COLOR_VALUE_BOUNDARY; it++) {
            redValuesCount[it] = 0;
            greenValuesCount[it] = 0;
            blueValuesCount[it] = 0;
            grayscaleValuesCount[it] = 0;
        }
    }

    private void countColorValues() {
        for(int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                int pixel = imageBitmap.getPixel(row, col);
                int red = Color.red(pixel);
                int blue = Color.blue(pixel);
                int green = Color.green(pixel);
                int grayscale = ((red + green + blue) / 3) % 256;

                redValuesCount[red]++;
                greenValuesCount[green]++;
                blueValuesCount[blue]++;
                grayscaleValuesCount[grayscale]++;
            }
        }
    }
}
