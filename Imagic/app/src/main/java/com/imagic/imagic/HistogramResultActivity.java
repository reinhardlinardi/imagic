package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private ProgressBar loadingBar;
    private BarChart redBarChart;
    private BarChart greenBarChart;
    private BarChart blueBarChart;
    private BarChart grayscaleBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram_result);

        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        redBarChart = (BarChart) findViewById(R.id.red_bar_chart);
        greenBarChart = (BarChart) findViewById(R.id.green_bar_chart);
        blueBarChart = (BarChart) findViewById(R.id.blue_bar_chart);
        grayscaleBarChart = (BarChart) findViewById(R.id.grayscale_bar_chart);

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
        plotArrayToHistogram();
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
                int pixel = imageBitmap.getPixel(col, row);
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

    private void plotArrayToHistogram() {
        List<BarEntry> redBarEntries = new ArrayList<>();
        List<BarEntry> greenBarEntries = new ArrayList<>();
        List<BarEntry> blueBarEntries = new ArrayList<>();
        List<BarEntry> grayscaleBarEntries = new ArrayList<>();

        for(int it = 0; it < COLOR_VALUE_BOUNDARY; it++) {
            redBarEntries.add(new BarEntry(it, redValuesCount[it]));
            greenBarEntries.add(new BarEntry(it, greenValuesCount[it]));
            blueBarEntries.add(new BarEntry(it, blueValuesCount[it]));
            grayscaleBarEntries.add(new BarEntry(it, grayscaleValuesCount[it]));
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
