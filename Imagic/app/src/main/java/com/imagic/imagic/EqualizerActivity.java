package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.lang.reflect.Method;

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

//                UI.show(redGraphView);
//                UI.show(greenGraphView);
//                UI.show(blueGraphView);
//
//                UI.renderGraphView(redGraphView, originalImage.rgb.red.series);
//                UI.renderGraphView(greenGraphView, originalImage.rgb.green.series);
//                UI.renderGraphView(blueGraphView, originalImage.rgb.blue.series);

                UI.enable(enhanceButton);
            }
            else {
//                ContrastEnhancementActivity.HistogramGenerationTask histogramGenerationTask = new ContrastEnhancementActivity.HistogramGenerationTask();
//                histogramGenerationTask.execute(Image.ColorType.RED, Image.ColorType.GREEN, Image.ColorType.BLUE);
            }
        }
    }

    // Contrast enhancement async task
//    private class ContrastEnhancementTask extends AsyncTask<Void, Integer, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            int numTransformations = 3;
//            int done = 0;
//            publishProgress(countProgress(done + 1, numTransformations + 2));
//
//            try {
//                Method method = transformedImage.rgb.red.getClass().getSuperclass().getMethod(selectedOption.executeFunctionOnButtonClick, double.class);
//
//                int[] newRedValue = (int[]) method.invoke(transformedImage.rgb.red, (double)(redPercentage)/100);
//                publishProgress(countProgress((++done) + 1, numTransformations + 2));
//
//                int[] newGreenValue = (int[]) method.invoke(transformedImage.rgb.green, (double)(greenPercentage)/100);
//                publishProgress(countProgress((++done) + 1, numTransformations + 2));
//
//                int[] newBlueValue = (int[]) method.invoke(transformedImage.rgb.blue, (double)(bluePercentage)/100);
//                publishProgress(countProgress((++done) + 1, numTransformations + 2));
//
//                transformedImage.updateBitmap(ContrastEnhancementActivity.this, newRedValue, newGreenValue, newBlueValue);
//                publishProgress(countProgress((++done) + 2, numTransformations + 2));
//
//                if(isCancelled()) return null;
//            }
//            catch(Exception e) {
//                Log.e("Imagic", "Exception", e);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            try {
//                transformedImage = new Image(ContrastEnhancementActivity.this, originalImage, false);
//            }
//            catch(Exception e) {
//                Log.e("Imagic", "Exception", e);
//            }
//
//            UI.disable(enhanceButton);
//            progressBar.setProgress(0);
//            UI.show(progressBar);
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }
//
//        @Override
//        protected void onPostExecute(Void results) {
//            transformedImage.rgb.enableValueDependentColor();
//
//            UI.renderGraphView(redGraphView, transformedImage.rgb.red.series);
//            UI.renderGraphView(greenGraphView, transformedImage.rgb.green.series);
//            UI.renderGraphView(blueGraphView, transformedImage.rgb.blue.series);
//
//            UI.updateImageView(ContrastEnhancementActivity.this, transformedImage.bitmap, afterView);
//            UI.clearImageViewMemory(ContrastEnhancementActivity.this);
//
//            UI.setInvisible(progressBar);
//            UI.enable(enhanceButton);
//        }
//    }

    //PROPERTIES
    // Cached image data URI
    private Uri cachedImageDataURI;

    // Image
    private Image originalImage;
    private Image transformedImage;

    // UI components
    private ProgressBar progressBar;

    private ImageView beforeView;
    private ImageView afterView;

    private GraphView redGraphView_before;
    private GraphView greenGraphView_after;
    private GraphView blueGraphView_before;
    private GraphView redGraphView_after;
    private GraphView greenGraphView_before;
    private GraphView blueGraphView_after;

    private SeekBar firstPointSeekBar_y;
    private SeekBar secondPointSeekBar_x;
    private SeekBar secondPointSeekBar_y;
    private SeekBar thirdPointSeekBar_x;
    private SeekBar thirdPointSeekBar_y;
    private SeekBar fourthPointSeekBar_y;

    private Button enhanceButton;

    // SeekBar percentage value
    private int firstPointPercentage_y;
    private int secondPointPercentage_x;
    private int secondPointPercentage_y;
    private int thirdPointPercentage_x;
    private int thirdPointPercentage_y;
    private int fourthPointPercentage_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        //Initialize UI Component
        progressBar = findViewById(R.id.equalizerProgressBar);

        beforeView = findViewById(R.id.equalizerImageBefore);
        afterView = findViewById(R.id.equalizerImageAfter);

        firstPointSeekBar_y = findViewById(R.id.y_firstPointEqualizerSeekBar);
        secondPointSeekBar_x = findViewById(R.id.x_secondPointEqualizerSeekBar);
        secondPointSeekBar_y = findViewById(R.id.y_secondPointEqualizerSeekBar);
        thirdPointSeekBar_x = findViewById(R.id.x_thirdPointEqualizerSeekBar);
        thirdPointSeekBar_y = findViewById(R.id.y_thirdPointEqualizerSeekBar);
        fourthPointSeekBar_y = findViewById(R.id.y_fourthPointEqualizerSeekBar);

        enhanceButton = findViewById(R.id.enhanceContrastButton);

        TextView firstPointTextView_y = findViewById(R.id.y_firstPointEqualizerTextView);
        TextView secondPointTextView_x = findViewById(R.id.x_secondPointEqualizerTextView);
        TextView secondPointTextView_y = findViewById(R.id.y_secondPointEqualizerTextView);
        TextView thirdPointTextView_x = findViewById(R.id.x_thirdPointEqualizerTextView);
        TextView thirdPointTextView_y = findViewById(R.id.y_thirdPointEqualizerTextView);
        TextView fourthPointTextView_y = findViewById(R.id.y_fourthPointEqualizerTextView);

        redGraphView_before = findViewById(R.id.before_redGraphView);
        redGraphView_after = findViewById(R.id.after_redGraphView);
        greenGraphView_before = findViewById(R.id.before_greenGraphView);
        greenGraphView_after = findViewById(R.id.after_greenGraphView);
        blueGraphView_before = findViewById(R.id.before_blueGraphView);
        blueGraphView_after = findViewById(R.id.after_blueGraphView);

        //Setting On Click Listener
        enhanceButton.setOnClickListener(getButtonOnClickListener());

        firstPointSeekBar_y.setOnSeekBarChangeListener(getSeekBarOnChangeListener(firstPointTextView_y));
        secondPointSeekBar_x.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextView_x));
        secondPointSeekBar_y.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextView_y));
        thirdPointSeekBar_x.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextView_x));
        thirdPointSeekBar_y.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextView_y));
        fourthPointSeekBar_y.setOnSeekBarChangeListener(getSeekBarOnChangeListener(fourthPointTextView_y));



        //Percentage Assignment
        firstPointPercentage_y = 100;
        secondPointPercentage_x = 100;
        secondPointPercentage_y = 100;
        thirdPointPercentage_x = 100;
        thirdPointPercentage_y = 100;
        fourthPointPercentage_y = 100;
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
                    if(seekBar == firstPointSeekBar_y) firstPointPercentage_y = progress;
                    else if(seekBar == secondPointSeekBar_x) secondPointPercentage_x = progress;
                    else if(seekBar == secondPointSeekBar_y) secondPointPercentage_y = progress;
                    else if(seekBar == thirdPointSeekBar_x) thirdPointPercentage_x = progress;
                    else if(seekBar == thirdPointSeekBar_y) thirdPointPercentage_y = progress;
                    else if(seekBar == fourthPointSeekBar_y) fourthPointPercentage_y = progress;

                    textView.setText(Integer.toString(progress) + "%");
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
                //Implement here
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
