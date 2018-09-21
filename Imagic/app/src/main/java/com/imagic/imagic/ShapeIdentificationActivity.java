package com.imagic.imagic;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

public class ShapeIdentificationActivity extends AppCompatActivity{

    // Image load async task
    private class ImageLoadTask extends AsyncTask<Uri, Integer, Void> {
        @Override
        protected Void doInBackground(Uri... URIs) {
            int numImages = URIs.length * 2;
            int done = 0;
            publishProgress(countProgress(done + 1, numImages + 1));

            for(Uri URI : URIs) {
                try {
                    Image noBitmapImage = JSONSerializer.deserialize(ShapeIdentificationActivity.this, Cache.read(URI), Image.class);

                    originalImage = new Image(ShapeIdentificationActivity.this, noBitmapImage, true);
                    publishProgress(countProgress((++done) + 1, numImages + 1));

                    if(isCancelled()) break;
                }
                catch(Exception e) {
                    Log.e("Imagic", "Exception", e);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            UI.updateImageView(ShapeIdentificationActivity.this, originalImage.uri, beforeView);
            UI.clearImageViewMemory(ShapeIdentificationActivity.this);
            UI.setInvisible(progressBar);

            if(dataAvailableInCache()) {
                originalImage.rgb.enableValueDependentColor();
            }
        }
    }

    // Contrast enhancement async task
    private class ChromaticTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ChainCode chainCode = new ChainCode();
//            int[][] test = new int[][]{
//                    {0, 0, 0, 0, 0, 0, 0, 0},
//                    {0, 0, 1, 0, 0, 0, 0, 0},
//                    {0, 0, 1, 1, 1, 1, 0, 0},
//                    {0, 0, 1, 1, 1, 0, 0, 0},
//                    {0, 1, 1, 1, 1, 0, 0, 0},
//                    {0, 0, 0, 0, 1, 0, 0, 0},
//                    {0, 0, 0, 0, 0, 0, 0, 0},
//                    {0, 0, 0, 0, 0, 0, 0, 0},
//            };
            int[][] matrix = originalImage.getChromaticMatrix();
            Log.v("res", Arrays.deepToString(matrix));
            chainCode.countDirectionCode(matrix);
            prediction = chainCode.predict();

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            predictionResultView.setText(Integer.toString(prediction));
            UI.setInvisible(progressBar);
        }
    }

    // Cached image data URI
    private Uri cachedImageDataURI;

    // Image
    private Image originalImage;

    // UI components
    private ProgressBar progressBar;
    private ImageView beforeView;
    private ImageView afterView;
    private TextView predictionResultView;

    int prediction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_identification);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.shapeIdentificationProgressBar);
        beforeView = findViewById(R.id.shapeIdentificationImageBefore);
        predictionResultView = findViewById(R.id.shapeIdentificationVerdict);

        ShapeIdentificationActivity.ImageLoadTask imageLoadTask = new ShapeIdentificationActivity.ImageLoadTask();
        imageLoadTask.execute(cachedImageDataURI);

        ShapeIdentificationActivity.ChromaticTask chromaticTask = new ShapeIdentificationActivity.ChromaticTask();
        chromaticTask.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI.clearImageViewMemory(this);
    }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return !(originalImage.rgb.isDataEmpty()); }
}
