package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
            int[][] coefficients = new int[3][3];
            int[] rightHandSide = new int[3];

            coefficients[0][0] = secondPointPercentageX * secondPointPercentageX * secondPointPercentageX;
            coefficients[0][1] = secondPointPercentageX * secondPointPercentageX;
            coefficients[0][2] = secondPointPercentageX;
            coefficients[1][0] = thirdPointPercentageX * thirdPointPercentageX * thirdPointPercentageX;
            coefficients[1][1] = thirdPointPercentageX * thirdPointPercentageX;
            coefficients[1][2] = thirdPointPercentageX;
            coefficients[2][0] = 255 * 255 * 255;
            coefficients[2][1] = 255 * 255;
            coefficients[2][2] = 255;

            rightHandSide[0] = secondPointPercentageY - firstPointPercentageY;
            rightHandSide[1] = thirdPointPercentageY - firstPointPercentageY;
            rightHandSide[2] = fourthPointPercentageY - firstPointPercentageY;

            LinearEquation linearEquation = new LinearEquation(3);
            linearEquation.setCoefficients(coefficients);
            linearEquation.setRightHandSide(rightHandSide);
            linearEquation.solve();

            Histogram histogram = new Histogram();
            for(int idx = 0; idx < 256; idx++) histogram.addDataPoint(idx, linearEquation.compute(idx));

            for(DataPoint dp : histogram.dataPoints) {
                Log.d("Data", Double.toString(dp.getX()) + " " + Double.toString(dp.getY()));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            /*
            try {
                transformedImage = new Image(ContrastEnhancementActivity.this, originalImage, false);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
            */
            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            /*
            transformedImage.rgb.enableValueDependentColor();

            UI.renderGraphView(redGraphView, transformedImage.rgb.red.series);
            UI.renderGraphView(greenGraphView, transformedImage.rgb.green.series);
            UI.renderGraphView(blueGraphView, transformedImage.rgb.blue.series);

            UI.updateImageView(ContrastEnhancementActivity.this, transformedImage.bitmap, afterView);
            UI.clearImageViewMemory(ContrastEnhancementActivity.this);
            */
            UI.setInvisible(progressBar);
        }
    }

    // Cached image data URI
    private Uri cachedImageDataURI;

    // Image
    private Image originalImage;
    private Image transformedImage;

    // UI components
    private ProgressBar progressBar;

    private ImageView beforeView;
    private ImageView afterView;

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

    // SeekBar percentage value
    private int firstPointPercentageY;
    private int secondPointPercentageX;
    private int secondPointPercentageY;
    private int thirdPointPercentageX;
    private int thirdPointPercentageY;
    private int fourthPointPercentageY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        //Initialize UI Component
        progressBar = findViewById(R.id.equalizerProgressBar);

        beforeView = findViewById(R.id.equalizerImageBefore);
        afterView = findViewById(R.id.equalizerImageAfter);

        firstPointSeekBarY = findViewById(R.id.y_firstPointEqualizerSeekBar);
        secondPointSeekBarX = findViewById(R.id.x_secondPointEqualizerSeekBar);
        secondPointSeekBarY = findViewById(R.id.y_secondPointEqualizerSeekBar);
        thirdPointSeekBarX = findViewById(R.id.x_thirdPointEqualizerSeekBar);
        thirdPointSeekBarY = findViewById(R.id.y_thirdPointEqualizerSeekBar);
        fourthPointSeekBarY = findViewById(R.id.y_fourthPointEqualizerSeekBar);

        TextView firstPointTextViewY = findViewById(R.id.y_firstPointEqualizerTextView);
        TextView secondPointTextViewX = findViewById(R.id.x_secondPointEqualizerTextView);
        TextView secondPointTextViewY = findViewById(R.id.y_secondPointEqualizerTextView);
        TextView thirdPointTextViewX = findViewById(R.id.x_thirdPointEqualizerTextView);
        TextView thirdPointTextViewY = findViewById(R.id.y_thirdPointEqualizerTextView);
        TextView fourthPointTextViewY = findViewById(R.id.y_fourthPointEqualizerTextView);

        redGraphViewBefore = findViewById(R.id.before_redGraphView);
        redGraphViewAfter = findViewById(R.id.after_redGraphView);
        greenGraphViewBefore = findViewById(R.id.before_greenGraphView);
        greenGraphViewAfter = findViewById(R.id.after_greenGraphView);
        blueGraphViewBefore = findViewById(R.id.before_blueGraphView);
        blueGraphViewAfter = findViewById(R.id.after_blueGraphView);

        firstPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(firstPointTextViewY));
        secondPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextViewX));
        secondPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextViewY));
        thirdPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextViewX));
        thirdPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextViewY));
        fourthPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(fourthPointTextViewY));

        //Percentage Assignment
        firstPointPercentageY = 100;
        secondPointPercentageX = 255;
        secondPointPercentageY = 100;
        thirdPointPercentageX = 255;
        thirdPointPercentageY = 100;
        fourthPointPercentageY = 100;

        UI.hide(redGraphViewBefore);
        UI.hide(redGraphViewAfter);
        UI.hide(greenGraphViewBefore);
        UI.hide(greenGraphViewAfter);
        UI.hide(blueGraphViewBefore);
        UI.hide(blueGraphViewAfter);

        UI.showAllXGraphView(redGraphViewBefore);
        UI.showAllXGraphView(redGraphViewAfter);
        UI.showAllXGraphView(greenGraphViewBefore);
        UI.showAllXGraphView(greenGraphViewAfter);
        UI.showAllXGraphView(blueGraphViewBefore);
        UI.showAllXGraphView(blueGraphViewAfter);

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
                    if(seekBar == firstPointSeekBarY) firstPointPercentageY = progress;
                    else if(seekBar == secondPointSeekBarX) secondPointPercentageX = progress;
                    else if(seekBar == secondPointSeekBarY) secondPointPercentageY = progress;
                    else if(seekBar == thirdPointSeekBarX) thirdPointPercentageX = progress;
                    else if(seekBar == thirdPointSeekBarY) thirdPointPercentageY = progress;
                    else if(seekBar == fourthPointSeekBarY) fourthPointPercentageY = progress;

                    textView.setText(Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    public View.OnClickListener getButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
