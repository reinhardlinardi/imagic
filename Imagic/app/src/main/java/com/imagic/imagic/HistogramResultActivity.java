package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistogramResultActivity extends AppCompatActivity {

    // Constants
    private static final int NUM_OF_COLOR_VALUE = 256;

    // Image
    private Bitmap imageBitmap;
    private int imageWidth;
    private int imageHeight;

    // Counters
    private int[] redValuesCount = new int[NUM_OF_COLOR_VALUE];
    private int[] greenValuesCount = new int[NUM_OF_COLOR_VALUE];
    private int[] blueValuesCount = new int[NUM_OF_COLOR_VALUE];
    private int[] grayscaleValuesCount = new int[NUM_OF_COLOR_VALUE];

    // Charts and progress bar
    private ProgressBar loadingBar;
    private BarChart redBarChart;
    private BarChart greenBarChart;
    private BarChart blueBarChart;
    private BarChart grayscaleBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_result);

        loadingBar = findViewById(R.id.loading_bar);
        redBarChart = findViewById(R.id.red_bar_chart);
        greenBarChart = findViewById(R.id.green_bar_chart);
        blueBarChart = findViewById(R.id.blue_bar_chart);
        grayscaleBarChart = findViewById(R.id.grayscale_bar_chart);

        resetValuesCount();

        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra("imageUri");

        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageHeight = imageBitmap.getHeight();
        imageWidth = imageBitmap.getWidth();

        countColorValues();
        plotArrayToHistogram();
    }

    // Reset all counter
    private void resetValuesCount() {
        Arrays.fill(redValuesCount, 0);
        Arrays.fill(greenValuesCount, 0);
        Arrays.fill(blueValuesCount, 0);
        Arrays.fill(grayscaleValuesCount, 0);
    }

    // Count all colors
    private void countColorValues() {
        for(int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                int pixel = imageBitmap.getPixel(col, row);
                int red = Color.red(pixel);
                int blue = Color.blue(pixel);
                int green = Color.green(pixel);
                int grayscale = (red + green + blue) / 3;

                redValuesCount[red]++;
                greenValuesCount[green]++;
                blueValuesCount[blue]++;
                grayscaleValuesCount[grayscale]++;
            }
        }
    }

    // Plot color count to histogram
    private void plotArrayToHistogram() {
        List<BarEntry> redBarEntries = new ArrayList<>();
        List<BarEntry> greenBarEntries = new ArrayList<>();
        List<BarEntry> blueBarEntries = new ArrayList<>();
        List<BarEntry> grayscaleBarEntries = new ArrayList<>();

        for(int idx = 0; idx < NUM_OF_COLOR_VALUE; idx++) {
            redBarEntries.add(new BarEntry(idx, redValuesCount[idx]));
            greenBarEntries.add(new BarEntry(idx, greenValuesCount[idx]));
            blueBarEntries.add(new BarEntry(idx, blueValuesCount[idx]));
            grayscaleBarEntries.add(new BarEntry(idx, grayscaleValuesCount[idx]));
        }

        BarDataSet redBarDataSet = new BarDataSet(redBarEntries, "Red");
        BarDataSet greenBarDataSet = new BarDataSet(greenBarEntries, "Green");
        BarDataSet blueBarDataSet = new BarDataSet(blueBarEntries, "Blue");
        BarDataSet grayscaleBarDataSet = new BarDataSet(grayscaleBarEntries, "Grayscale");

        redBarDataSet.setColor(Color.RED);
        greenBarDataSet.setColor(Color.GREEN);
        blueBarDataSet.setColor(Color.BLUE);
        grayscaleBarDataSet.setColor(Color.LTGRAY);

        BarData redBarData = new BarData(redBarDataSet);
        BarData greenBarData = new BarData(greenBarDataSet);
        BarData blueBarData = new BarData(blueBarDataSet);
        BarData grayscaleBarData = new BarData(grayscaleBarDataSet);

        redBarChart.setData(redBarData);
        greenBarChart.setData(greenBarData);
        blueBarChart.setData(blueBarData);
        grayscaleBarChart.setData(grayscaleBarData);

        redBarChart.invalidate();
        greenBarChart.invalidate();
        blueBarChart.invalidate();
        grayscaleBarChart.invalidate();
    }
}
