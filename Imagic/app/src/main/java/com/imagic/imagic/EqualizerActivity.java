package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

public class EqualizerActivity extends AppCompatActivity {

    // Image load async task
    private class ImageLoadTask extends AsyncTask<Uri, Integer, Void> {
        @Override
        protected Void doInBackground(Uri... URIs) {
            int numImages = URIs.length * 2;
            int done = 0;
            publishProgress(countProgress(done + 1, numImages + 1));

            for(Uri URI : URIs) {
                try {
                    Image noBitmapImage = JSONSerializer.deserialize(EqualizerActivity.this, Cache.read(URI), Image.class);

                    originalImage = new Image(EqualizerActivity.this, noBitmapImage, true);
                    publishProgress(countProgress((++done) + 1, numImages + 1));

                    transformedImage = new Image(EqualizerActivity.this, originalImage, false);
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
            UI.disable(firstPointSeekBarY);
            UI.disable(secondPointSeekBarX);
            UI.disable(secondPointSeekBarY);
            UI.disable(thirdPointSeekBarX);
            UI.disable(thirdPointSeekBarY);
            UI.disable(fourthPointSeekBarY);

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            UI.updateImageView(EqualizerActivity.this, originalImage.uri, beforeView);
            UI.updateImageView(EqualizerActivity.this, transformedImage.uri, afterView);
            UI.clearImageViewMemory(EqualizerActivity.this);
            UI.setInvisible(progressBar);

            if(dataAvailableInCache()) {
                originalImage.rgb.enableValueDependentColor();

                UI.show(redGraphViewBefore);
                UI.show(redGraphViewAfter);
                UI.show(greenGraphViewBefore);
                UI.show(greenGraphViewAfter);
                UI.show(blueGraphViewBefore);
                UI.show(blueGraphViewAfter);

                UI.enable(firstPointSeekBarY);
                UI.enable(secondPointSeekBarX);
                UI.enable(secondPointSeekBarY);
                UI.enable(thirdPointSeekBarX);
                UI.enable(thirdPointSeekBarY);
                UI.enable(fourthPointSeekBarY);

                UI.renderGraphView(redGraphViewBefore, originalImage.rgb.red.series);
                UI.renderGraphView(redGraphViewAfter, originalImage.rgb.red.series);
                UI.renderGraphView(greenGraphViewBefore, originalImage.rgb.green.series);
                UI.renderGraphView(greenGraphViewAfter, originalImage.rgb.green.series);
                UI.renderGraphView(blueGraphViewBefore, originalImage.rgb.blue.series);
                UI.renderGraphView(blueGraphViewAfter, originalImage.rgb.blue.series);
            }
            else {
                HistogramGenerationTask histogramGenerationTask = new HistogramGenerationTask();
                histogramGenerationTask.execute(Image.ColorType.RED, Image.ColorType.GREEN, Image.ColorType.BLUE);
            }
        }
    }

    // Histogram async task
    private class HistogramGenerationTask extends AsyncTask<Image.ColorType, Integer, Void> {
        @Override
        protected Void doInBackground(Image.ColorType... colorTypes) {
            int numColors = colorTypes.length;
            int done = 0;
            publishProgress(countProgress(done + 1, numColors + 1));

            for(Image.ColorType colorType : colorTypes) {
                originalImage.generateHistogramByColorType(colorType);
                publishProgress(countProgress((++done) + 1, numColors + 1));

                if(isCancelled()) break;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            UI.hide(redGraphViewBefore);
            UI.hide(redGraphViewAfter);
            UI.hide(greenGraphViewBefore);
            UI.hide(greenGraphViewAfter);
            UI.hide(blueGraphViewBefore);
            UI.hide(blueGraphViewAfter);

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            originalImage.rgb.enableValueDependentColor();

            UI.show(redGraphViewBefore);
            UI.show(redGraphViewAfter);
            UI.show(greenGraphViewBefore);
            UI.show(greenGraphViewAfter);
            UI.show(blueGraphViewBefore);
            UI.show(blueGraphViewAfter);

            UI.enable(firstPointSeekBarY);
            UI.enable(secondPointSeekBarX);
            UI.enable(secondPointSeekBarY);
            UI.enable(thirdPointSeekBarX);
            UI.enable(thirdPointSeekBarY);
            UI.enable(fourthPointSeekBarY);

            UI.renderGraphView(redGraphViewBefore, originalImage.rgb.red.series);
            UI.renderGraphView(redGraphViewAfter, originalImage.rgb.red.series);
            UI.renderGraphView(greenGraphViewBefore, originalImage.rgb.green.series);
            UI.renderGraphView(greenGraphViewAfter, originalImage.rgb.green.series);
            UI.renderGraphView(blueGraphViewBefore, originalImage.rgb.blue.series);
            UI.renderGraphView(blueGraphViewAfter, originalImage.rgb.blue.series);

            try {
                Cache.write(cachedImageDataURI, JSONSerializer.serialize(originalImage));
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            UI.setInvisible(progressBar);
        }
    }

    // Equalizer async task
    private class EqualizerTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int done = 0;

            double[][] coefficients = new double[3][3];
            double[] rightHandSide = new double[3];

            for(int row = 0; row < 3; row++) {
                double value = (row == 2)? 255 : XSeekBarValue[row];
                for(int col = 0; col < 3; col++) coefficients[row][col] = Math.pow(value, 3 - col);

                rightHandSide[row] = YSeekBarValue[row + 1] - YSeekBarValue[0];
            }

            LinearEquation linearEquation = new LinearEquation(3, YSeekBarValue[0]);
            linearEquation.setCoefficients(coefficients);
            linearEquation.setRightHandSide(rightHandSide);
            linearEquation.solve();
            publishProgress(countProgress(++done, 3));

            userDefinedHistogram = new Histogram();

            for(int idx = 0; idx < 256; idx++) {
                userDefinedHistogram.addDataPoint(idx, Math.abs(linearEquation.compute(idx)));
            }

            userDefinedHistogram.updateSeries();
            double[] userDefinedCDF = userDefinedHistogram.getCDF();

            int[] newRedValue = transformedImage.rgb.red.matchHistogram(userDefinedCDF);
            int[] newGreenValue = transformedImage.rgb.green.matchHistogram(userDefinedCDF);
            int[] newBlueValue = transformedImage.rgb.blue.matchHistogram(userDefinedCDF);

            publishProgress(countProgress(++done, 3));

            try {
                transformedImage.updateBitmap(EqualizerActivity.this, newRedValue, newGreenValue, newBlueValue);
                publishProgress(countProgress(++done, 3));
            }
            catch (Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            UI.disable(firstPointSeekBarY);
            UI.disable(secondPointSeekBarX);
            UI.disable(secondPointSeekBarY);
            UI.disable(thirdPointSeekBarX);
            UI.disable(thirdPointSeekBarY);
            UI.disable(fourthPointSeekBarY);

            try {
                transformedImage = new Image(EqualizerActivity.this, originalImage, false);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            UI.renderGraphView(userDefinedGraphView, userDefinedHistogram.series);

            transformedImage.rgb.enableValueDependentColor();

            UI.renderGraphView(redGraphViewAfter, transformedImage.rgb.red.series);
            UI.renderGraphView(greenGraphViewAfter, transformedImage.rgb.green.series);
            UI.renderGraphView(blueGraphViewAfter, transformedImage.rgb.blue.series);

            UI.updateImageView(EqualizerActivity.this, transformedImage.bitmap, afterView);
            UI.clearImageViewMemory(EqualizerActivity.this);

            UI.enable(firstPointSeekBarY);
            UI.enable(secondPointSeekBarX);
            UI.enable(secondPointSeekBarY);
            UI.enable(thirdPointSeekBarX);
            UI.enable(thirdPointSeekBarY);
            UI.enable(fourthPointSeekBarY);

            UI.setInvisible(progressBar);
        }
    }

    // Cached image data URI
    private Uri cachedImageDataURI;

    // Image
    private Image originalImage;
    private Image transformedImage;
    private Histogram userDefinedHistogram;

    // UI components
    private ProgressBar progressBar;

    private ImageView beforeView;
    private ImageView afterView;

    private GraphView userDefinedGraphView;
    private GraphView redGraphViewBefore;
    private GraphView greenGraphViewAfter;
    private GraphView blueGraphViewBefore;
    private GraphView redGraphViewAfter;
    private GraphView greenGraphViewBefore;
    private GraphView blueGraphViewAfter;

    private SeekBar firstPointSeekBarY;
    private SeekBar secondPointSeekBarX;
    private SeekBar secondPointSeekBarY;
    private SeekBar thirdPointSeekBarX;
    private SeekBar thirdPointSeekBarY;
    private SeekBar fourthPointSeekBarY;

    // SeekBar value
    private int[] XSeekBarValue = new int[2];
    private int[] YSeekBarValue = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        // Initialize UI Component
        progressBar = findViewById(R.id.equalizerProgressBar);

        beforeView = findViewById(R.id.equalizerImageBefore);
        afterView = findViewById(R.id.equalizerImageAfter);

        firstPointSeekBarY = findViewById(R.id.firstPointEqualizerSeekBarY);
        secondPointSeekBarX = findViewById(R.id.secondPointEqualizerSeekBarX);
        secondPointSeekBarY = findViewById(R.id.secondPointEqualizerSeekBarY);
        thirdPointSeekBarX = findViewById(R.id.thirdPointEqualizerSeekBarX);
        thirdPointSeekBarY = findViewById(R.id.thirdPointEqualizerSeekBarY);
        fourthPointSeekBarY = findViewById(R.id.fourthPointEqualizerSeekBarY);

        TextView firstPointTextViewY = findViewById(R.id.firstPointEqualizerTextViewY);
        TextView secondPointTextViewX = findViewById(R.id.secondPointEqualizerTextViewX);
        TextView secondPointTextViewY = findViewById(R.id.secondPointEqualizerTextViewY);
        TextView thirdPointTextViewX = findViewById(R.id.thirdPointEqualizerTextViewX);
        TextView thirdPointTextViewY = findViewById(R.id.thirdPointEqualizerTextViewY);
        TextView fourthPointTextViewY = findViewById(R.id.fourthPointEqualizerTextViewY);

        userDefinedGraphView = findViewById(R.id.graphViewUserDefined);
        userDefinedGraphView.getViewport().setMinY(0);
        userDefinedGraphView.getViewport().setMaxY(200);
        userDefinedGraphView.getViewport().setYAxisBoundsManual(true);

        redGraphViewBefore = findViewById(R.id.redGraphViewBefore);
        redGraphViewAfter = findViewById(R.id.redGraphViewAfter);
        greenGraphViewBefore = findViewById(R.id.greenGraphViewBefore);
        greenGraphViewAfter = findViewById(R.id.greenGraphViewAfter);
        blueGraphViewBefore = findViewById(R.id.blueGraphViewBefore);
        blueGraphViewAfter = findViewById(R.id.blueGraphViewAfter);

        firstPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(firstPointTextViewY));
        secondPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextViewX));
        secondPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextViewY));
        thirdPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextViewX));
        thirdPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextViewY));
        fourthPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(fourthPointTextViewY));

        // Percentage Assignment
        XSeekBarValue[0] = 86;
        XSeekBarValue[1] = 172;

        for(int idx = 0; idx < 4; idx++) YSeekBarValue[idx] = 100;

        UI.hide(userDefinedGraphView);
        UI.hide(redGraphViewBefore);
        UI.hide(redGraphViewAfter);
        UI.hide(greenGraphViewBefore);
        UI.hide(greenGraphViewAfter);
        UI.hide(blueGraphViewBefore);
        UI.hide(blueGraphViewAfter);

        UI.showAllXGraphView(userDefinedGraphView);
        UI.showAllXGraphView(redGraphViewBefore);
        UI.showAllXGraphView(redGraphViewAfter);
        UI.showAllXGraphView(greenGraphViewBefore);
        UI.showAllXGraphView(greenGraphViewAfter);
        UI.showAllXGraphView(blueGraphViewBefore);
        UI.showAllXGraphView(blueGraphViewAfter);

        userDefinedHistogram = new Histogram();
        for(int idx = 0; idx < 256; idx++) userDefinedHistogram.addDataPoint(idx, 100);
        userDefinedHistogram.updateSeries();

        UI.renderGraphView(userDefinedGraphView, userDefinedHistogram.series);
        UI.show(userDefinedGraphView);

        ImageLoadTask imageLoadTask = new ImageLoadTask();
        imageLoadTask.execute(cachedImageDataURI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI.clearImageViewMemory(this);
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(seekBar == firstPointSeekBarY || seekBar == secondPointSeekBarY || seekBar == thirdPointSeekBarY || seekBar == fourthPointSeekBarY) {
                        if(seekBar == firstPointSeekBarY) YSeekBarValue[0] = progress;
                        else if(seekBar == secondPointSeekBarY) YSeekBarValue[1] = progress;
                        else if(seekBar == thirdPointSeekBarY) YSeekBarValue[2] = progress;
                        else YSeekBarValue[3] = progress;

                        textView.setText(Integer.toString(progress));
                    }
                    else if(seekBar == secondPointSeekBarX) {
                        XSeekBarValue[0] = progress + 1;
                        textView.setText(Integer.toString(progress + 1));
                    }
                    else if(seekBar == thirdPointSeekBarX) {
                        XSeekBarValue[1] = progress + 128;
                        textView.setText(Integer.toString(progress + 128));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                EqualizerTask equalizerTask = new EqualizerTask();
                equalizerTask.execute();
            }
        };
    }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return !(originalImage.rgb.isDataEmpty()); }
}
