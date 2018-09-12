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

    private Button enhanceButton;

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

        enhanceButton = findViewById(R.id.enhanceContrastButton);

        TextView firstPointTextView_y = findViewById(R.id.y_firstPointEqualizerTextView);
        TextView secondPointTextView_x = findViewById(R.id.x_secondPointEqualizerTextView);
        TextView secondPointTextView_y = findViewById(R.id.y_secondPointEqualizerTextView);
        TextView thirdPointTextView_x = findViewById(R.id.x_thirdPointEqualizerTextView);
        TextView thirdPointTextView_y = findViewById(R.id.y_thirdPointEqualizerTextView);
        TextView fourthPointTextView_y = findViewById(R.id.y_fourthPointEqualizerTextView);

        redGraphViewBefore = findViewById(R.id.before_redGraphView);
        redGraphViewAfter = findViewById(R.id.after_redGraphView);
        greenGraphViewBefore = findViewById(R.id.before_greenGraphView);
        greenGraphViewAfter = findViewById(R.id.after_greenGraphView);
        blueGraphViewBefore = findViewById(R.id.before_blueGraphView);
        blueGraphViewAfter = findViewById(R.id.after_blueGraphView);

        //Setting On Click Listener
        enhanceButton.setOnClickListener(getButtonOnClickListener());

        firstPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(firstPointTextView_y));
        secondPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextView_x));
        secondPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(secondPointTextView_y));
        thirdPointSeekBarX.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextView_x));
        thirdPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thirdPointTextView_y));
        fourthPointSeekBarY.setOnSeekBarChangeListener(getSeekBarOnChangeListener(fourthPointTextView_y));



        //Percentage Assignment
        firstPointPercentageY = 100;
        secondPointPercentageX = 100;
        secondPointPercentageY = 100;
        thirdPointPercentageX = 100;
        thirdPointPercentageY = 100;
        fourthPointPercentageY = 100;

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
