package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class HistogramActivity extends AppCompatActivity {

    // Async task
    private class HistogramGenerationTask extends AsyncTask<Image.ColorType, Integer, Void> {
        @Override
        protected Void doInBackground(Image.ColorType... colorTypes) {
            int numColors = colorTypes.length;
            int done = 0;
            publishProgress(countProgress(done + 1, numColors + 1));

            for(Image.ColorType colorType : colorTypes) {
                image.generateHistogramByColorType(colorType);

                done++;
                publishProgress(countProgress(done + 1, numColors + 1));

                if(isCancelled()) break;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            UI.hide(redGraphView);
            UI.hide(greenGraphView);
            UI.hide(blueGraphView);
            UI.hide(grayscaleGraphView);

            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            image.rgb.enableValueDependentColor();
            image.grayscale.enableValueDependentColor();

            UI.show(redGraphView);
            UI.show(greenGraphView);
            UI.show(blueGraphView);
            UI.show(grayscaleGraphView);

            UI.renderGraphView(redGraphView, image.rgb.red.series);
            UI.renderGraphView(greenGraphView, image.rgb.green.series);
            UI.renderGraphView(blueGraphView, image.rgb.blue.series);
            UI.renderGraphView(grayscaleGraphView, image.grayscale.series);

            try {
                Cache.write(cachedImageDataURI, JSONSerializer.serialize(image));
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            UI.hide(progressBar);
            showToastOnTaskCompletion();
        }

        // Count progress
        private int countProgress(int numTaskDone, int totalNumTask) {
            float taskDoneFraction = (float) numTaskDone / totalNumTask;
            return (int)(taskDoneFraction * 100);
        }
    }

    // Cached image data URI
    private static Uri cachedImageDataURI;

    // Image
    private Image image;

    // UI components
    private ProgressBar progressBar;

    private GraphView redGraphView;
    private GraphView greenGraphView;
    private GraphView blueGraphView;
    private GraphView grayscaleGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.histogramProgressBar);
        ImageView imageView = findViewById(R.id.histogramImageView);

        redGraphView = findViewById(R.id.redGraphView);
        greenGraphView = findViewById(R.id.greenGraphView);
        blueGraphView = findViewById(R.id.blueGraphView);
        grayscaleGraphView = findViewById(R.id.grayscaleGraphView);

        UI.showAllXGraphView(redGraphView);
        UI.showAllXGraphView(greenGraphView);
        UI.showAllXGraphView(blueGraphView);
        UI.showAllXGraphView(grayscaleGraphView);

        try {
            image = JSONSerializer.deserialize(getApplicationContext(), Cache.read(cachedImageDataURI), Image.class);
            UI.updateImageView(this, image.uri, imageView);

            if(dataAvailableInCache()) {
                image.rgb.enableValueDependentColor();
                image.grayscale.enableValueDependentColor();

                UI.renderGraphView(redGraphView, image.rgb.red.series);
                UI.renderGraphView(greenGraphView, image.rgb.green.series);
                UI.renderGraphView(blueGraphView, image.rgb.blue.series);
                UI.renderGraphView(grayscaleGraphView, image.grayscale.series);

                showToastOnTaskCompletion();
            }
            else {
                boolean rgbDataNotAvailable = !(rgbDataAvailableInCache());
                boolean grayscaleDataNotAvailable = !(grayscaleDataAvailableInCache());

                ArrayList<Image.ColorType> missingColorTypeData = new ArrayList<>();

                if(rgbDataNotAvailable) {
                    missingColorTypeData.add(Image.ColorType.RED);
                    missingColorTypeData.add(Image.ColorType.GREEN);
                    missingColorTypeData.add(Image.ColorType.BLUE);
                }

                if(grayscaleDataNotAvailable) missingColorTypeData.add(Image.ColorType.GRAYSCALE);

                HistogramGenerationTask histogramGenerationTask = new HistogramGenerationTask();
                histogramGenerationTask.execute(missingColorTypeData.toArray(new Image.ColorType[missingColorTypeData.size()]));
            }
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI.clearImageViewMemory(this);
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return rgbDataAvailableInCache() && grayscaleDataAvailableInCache(); }

    // Check if rgb data is available in cache
    private boolean rgbDataAvailableInCache() { return !(image.rgb.isDataEmpty()); }

    // Check if grayscale data is available in cache
    private boolean grayscaleDataAvailableInCache() { return !(image.grayscale.isDataEmpty()); }

    // Show toast on task completion
    private void showToastOnTaskCompletion() {
        Toast.makeText(this, "Histogram generated.", Toast.LENGTH_SHORT).show();
    }
}