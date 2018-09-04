package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

public class HistogramActivity extends AppCompatActivity {

    // Background task
    @SuppressLint("StaticFieldLeak")
    private class HistogramTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... colors) {
            int numColors = colors.length + 1;
            int numCount = 1;
            publishProgress(countProgress(numCount, numColors));

            for(int color : colors) {
                try {
                    int[] colorCount = Image.getColorCount(HistogramActivity.bitmap, color);

                    DataPoint[] dataPoint = new DataPoint[Image.MAX_POSSIBLE_COLORS];
                    for(int idx = 0; idx < colorCount.length; idx++) dataPoint[idx] = new DataPoint(idx, colorCount[idx]);

                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoint);

                    int graphViewID = getGraphViewID(color);
                    GraphView graphView = findViewById(graphViewID);
                    graphView.addSeries(series);

                    graphView.getViewport().setMinX(0);
                    graphView.getViewport().setMaxX(Image.MAX_POSSIBLE_COLORS - 1);
                    graphView.getViewport().setXAxisBoundsManual(true);

                    series.setValueDependentColor(getValueDependentColor(color));

                    numCount++;
                    publishProgress(countProgress(numCount, numColors));

                    if (isCancelled()) break;

                } catch (Exception e) {
                    Log.e("Imagic", "Exception", e);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            HistogramActivity.this.progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void results) {
            findViewById(R.id.redGraphView).setVisibility(View.VISIBLE);
            findViewById(R.id.greenGraphView).setVisibility(View.VISIBLE);
            findViewById(R.id.blueGraphView).setVisibility(View.VISIBLE);
            findViewById(R.id.grayscaleGraphView).setVisibility(View.VISIBLE);

            HistogramActivity.this.progressBar.setVisibility(View.GONE);
            Toast.makeText(HistogramActivity.this, "Histogram generated", Toast.LENGTH_SHORT).show();
        }

        // Count progress
        private int countProgress(int taskDone, int numTask) {
            float taskDoneFraction = (float)taskDone / numTask;
            return (int)(taskDoneFraction * 100);
        }

        // Get graph view ID
        private int getGraphViewID(int color) {
            int graphViewID = 0;

            switch(color) {
                case Image.COLOR_RED: graphViewID = R.id.redGraphView; break;
                case Image.COLOR_GREEN: graphViewID = R.id.greenGraphView; break;
                case Image.COLOR_BLUE: graphViewID = R.id.blueGraphView; break;
                case Image.COLOR_GRAYSCALE: graphViewID = R.id.grayscaleGraphView; break;
                default: break;
            }

            return graphViewID;
        }

        // Get data color
        private int getDataColor(DataPoint data, int color) {
            int dataColor = 0;

            if(color == Image.COLOR_GRAYSCALE) {
                float baseValue = 0.75f;
                float extraValue = ((float)((int)(data.getX()) + 1) / Image.MAX_POSSIBLE_COLORS) / 2;
                float totalValue = baseValue - extraValue;

                dataColor = Color.HSVToColor(new float[]{0.0f, 0.0f, totalValue});
            }
            else {
                float baseSaturation = 0.25f;
                float extraSaturation = ((float)((int)(data.getX()) + 1) / Image.MAX_POSSIBLE_COLORS) / 2;
                float totalSaturation = baseSaturation + extraSaturation;

                switch(color) {
                    case Image.COLOR_RED: dataColor = Color.HSVToColor(new float[]{0.0f, totalSaturation, 0.9f}); break;
                    case Image.COLOR_GREEN: dataColor = Color.HSVToColor(new float[]{120.0f, totalSaturation, 0.9f}); break;
                    case Image.COLOR_BLUE: dataColor = Color.HSVToColor(new float[]{240.0f, totalSaturation, 0.9f}); break;
                    default: break;
                }
            }

            return dataColor;
        }

        // Get value dependent color
        private ValueDependentColor<DataPoint> getValueDependentColor(final int color) {
            return new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return getDataColor(data, color);
                }
            };
        }
    }

    // Image bitmap
    private static Bitmap bitmap;

    // Progress bar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        findViewById(R.id.redGraphView).setVisibility(View.INVISIBLE);
        findViewById(R.id.greenGraphView).setVisibility(View.INVISIBLE);
        findViewById(R.id.blueGraphView).setVisibility(View.INVISIBLE);
        findViewById(R.id.grayscaleGraphView).setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            Uri imageURI = Uri.parse(bundle.getString("image"));

            progressBar = findViewById(R.id.histogramProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            try {
                HistogramActivity.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                HistogramActivity.HistogramTask histogramTask = new HistogramActivity.HistogramTask();
                histogramTask.execute(Image.COLOR_RED, Image.COLOR_GREEN, Image.COLOR_BLUE, Image.COLOR_GRAYSCALE);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }
}
