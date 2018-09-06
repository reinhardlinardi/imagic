package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HistogramActivity extends AppCompatActivity {

    // Background task
    @SuppressLint("StaticFieldLeak")
    private class HistogramTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Image.ColorType[] colorTypes = Image.ColorType.values();

            int done = 0;
            int numColors = colorTypes.length;
            publishProgress(Progress.countProgess(done + 1, numColors + 1));

            for(Image.ColorType colorType : colorTypes) {
                HistogramActivity.this.image.generateHistogramByColorType(colorType);

                done++;
                publishProgress(Progress.countProgess(done + 1, numColors + 1));

                if(isCancelled()) break;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            image.redHistogram.hide();
            image.greenHistogram.hide();
            image.blueHistogram.hide();
            image.grayscaleHistogram.hide();

            progressBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgess(progress); }

        @Override
        protected void onPostExecute(Void results) {
            image.redHistogram.enableValueDependentColor();
            image.greenHistogram.enableValueDependentColor();
            image.blueHistogram.enableValueDependentColor();
            image.grayscaleHistogram.enableValueDependentColor();

            image.redHistogram.show();
            image.greenHistogram.show();
            image.blueHistogram.show();
            image.grayscaleHistogram.show();

            image.redHistogram.render();
            image.greenHistogram.render();
            image.blueHistogram.render();
            image.grayscaleHistogram.render();

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(HistogramActivity.imageDataURI.getPath())))) {
                String json = image.jsonSerialize();
                writer.write(json);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            progressBar.hide();
            showToastOnTaskCompletion();
        }
    }

    // Internal shared cached image data URI
    private static Uri imageDataURI;

    // Image bitmap
    private Image image;

    // Progress bar
    private Progress progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            HistogramActivity.imageDataURI = Uri.parse(bundle.getString("imageData"));
            File imageDataFile = new File(HistogramActivity.imageDataURI.getPath());

            try(BufferedReader reader = new BufferedReader(new FileReader(imageDataFile))) {
                StringBuilder imageData = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) imageData.append(line);
                String json = imageData.toString();

                image = new Image();
                image.jsonDeserialize(this, json);

                if(dataAvailableInCache()) {
                    image.redHistogram.enableValueDependentColor();
                    image.greenHistogram.enableValueDependentColor();
                    image.blueHistogram.enableValueDependentColor();
                    image.grayscaleHistogram.enableValueDependentColor();

                    image.redHistogram.render();
                    image.greenHistogram.render();
                    image.blueHistogram.render();
                    image.grayscaleHistogram.render();

                    showToastOnTaskCompletion();
                }
                else {
                    image.redHistogram = new RedHistogram(this, R.id.redGraphView);
                    image.greenHistogram = new GreenHistogram(this, R.id.greenGraphView);
                    image.blueHistogram = new BlueHistogram(this, R.id.blueGraphView);
                    image.grayscaleHistogram = new GrayscaleHistogram(this, R.id.grayscaleGraphView);

                    progressBar = new Progress(this, R.id.histogramProgressBar);
                    HistogramActivity.HistogramTask histogramTask = new HistogramActivity.HistogramTask();
                    histogramTask.execute();
                }
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() {
        if(image.redHistogram.isUninitialized() || image.greenHistogram.isUninitialized() || image.blueHistogram.isUninitialized() || image.grayscaleHistogram.isUninitialized()) return false;
        else return true;
    }

    // Show toast on task completion
    private void showToastOnTaskCompletion() {
        Toast.makeText(this, "Histogram generated.", Toast.LENGTH_SHORT).show();
    }
}
