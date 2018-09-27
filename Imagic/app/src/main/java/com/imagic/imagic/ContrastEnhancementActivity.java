package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ContrastEnhancementActivity extends AppCompatActivity {

    // Image load async task
    private class ImageLoadTask extends AsyncTask<Uri, Integer, Void> {
        @Override
        protected Void doInBackground(Uri... URIs) {
            int numImages = URIs.length * 2;
            int done = 0;
            publishProgress(countProgress(done + 1, numImages + 1));

            for(Uri URI : URIs) {
                try {
                    Image noBitmapImage = JSONSerializer.deserialize(ContrastEnhancementActivity.this, Cache.read(URI), Image.class);

                    originalImage = new Image(ContrastEnhancementActivity.this, noBitmapImage, true);
                    publishProgress(countProgress((++done) + 1, numImages + 1));

                    transformedImage = new Image(ContrastEnhancementActivity.this, originalImage, false);
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
            UI.updateImageView(ContrastEnhancementActivity.this, originalImage.uri, beforeView);
            UI.updateImageView(ContrastEnhancementActivity.this, transformedImage.uri, afterView);
            UI.clearImageViewMemory(ContrastEnhancementActivity.this);
            UI.setInvisible(progressBar);

            if(dataAvailableInCache()) {
                originalImage.rgb.enableValueDependentColor();

                UI.show(redGraphView);
                UI.show(greenGraphView);
                UI.show(blueGraphView);

                UI.renderGraphView(redGraphView, originalImage.rgb.red.series);
                UI.renderGraphView(greenGraphView, originalImage.rgb.green.series);
                UI.renderGraphView(blueGraphView, originalImage.rgb.blue.series);

                UI.enable(enhanceButton);
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
            UI.hide(redGraphView);
            UI.hide(greenGraphView);
            UI.hide(blueGraphView);

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            originalImage.rgb.enableValueDependentColor();

            UI.show(redGraphView);
            UI.show(greenGraphView);
            UI.show(blueGraphView);

            UI.renderGraphView(redGraphView, originalImage.rgb.red.series);
            UI.renderGraphView(greenGraphView, originalImage.rgb.green.series);
            UI.renderGraphView(blueGraphView, originalImage.rgb.blue.series);

            try {
                Cache.write(cachedImageDataURI, JSONSerializer.serialize(originalImage));
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            UI.setInvisible(progressBar);
            UI.enable(enhanceButton);
        }
    }

    // Contrast enhancement async task
    private class ContrastEnhancementTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int numTransformations = 3;
            int done = 0;
            publishProgress(countProgress(done + 1, numTransformations + 2));

            int[] newRedValue = new int[256];
            int[] newGreenValue = new int[256];
            int[] newBlueValue = new int[256];

            ContrastEnhancementOption selectedOption = (ContrastEnhancementOption) algorithmSpinner.getSelectedItem();

            switch(selectedOption.algorithm) {
                case "Linear":
                    newRedValue = transformedImage.rgb.red.stretch((double) redPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newGreenValue = transformedImage.rgb.green.stretch((double) greenPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newBlueValue = transformedImage.rgb.blue.stretch((double) bluePercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    break;
                case "CDF":
                    newRedValue = transformedImage.rgb.red.cumulativeFrequencyEqualization((double) redPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newGreenValue = transformedImage.rgb.green.cumulativeFrequencyEqualization((double) greenPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newBlueValue = transformedImage.rgb.blue.cumulativeFrequencyEqualization((double) bluePercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    break;
                case "Logarithmic":
                    newRedValue = transformedImage.rgb.red.logarithmicEqualization((double) redPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newGreenValue = transformedImage.rgb.green.logarithmicEqualization((double) greenPercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    newBlueValue = transformedImage.rgb.blue.logarithmicEqualization((double) bluePercentage / 100);
                    publishProgress(countProgress((++done) + 1, numTransformations + 2));
                    break;
                default:
                    break;
            }

            try {
                transformedImage.updateBitmap(ContrastEnhancementActivity.this, newRedValue, newGreenValue, newBlueValue);
                publishProgress(countProgress((++done) + 1, numTransformations + 2));
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            try {
                transformedImage = new Image(ContrastEnhancementActivity.this, originalImage, false);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            UI.disable(algorithmSpinner);
            UI.disable(enhanceButton);

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            transformedImage.rgb.enableValueDependentColor();

            UI.renderGraphView(redGraphView, transformedImage.rgb.red.series);
            UI.renderGraphView(greenGraphView, transformedImage.rgb.green.series);
            UI.renderGraphView(blueGraphView, transformedImage.rgb.blue.series);

            UI.updateImageView(ContrastEnhancementActivity.this, transformedImage.bitmap, afterView);
            UI.clearImageViewMemory(ContrastEnhancementActivity.this);

            UI.setInvisible(progressBar);
            UI.enable(algorithmSpinner);
            UI.enable(enhanceButton);
        }
    }

    // Option adapter
    private class ContrastEnhancementAdapter extends ArrayAdapter<ContrastEnhancementOption> {

        ContrastEnhancementAdapter(ArrayList<ContrastEnhancementOption> options) {
            super(ContrastEnhancementActivity.this, R.layout.contrast_enhance_spinner_option, options);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = ContrastEnhancementActivity.this.getLayoutInflater();
            View optionView = inflater.inflate(R.layout.contrast_enhance_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.contrastEnhanceSpinnerOptionTextView);
            ContrastEnhancementOption option = getItem(position);

            if(option != null) optionTextView.setText(option.algorithm);

            return optionView;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            return getView(position, view, parent);
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
    private GraphView redGraphView;
    private GraphView greenGraphView;
    private GraphView blueGraphView;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private Spinner algorithmSpinner;
    private Button enhanceButton;

    // SeekBar percentage value
    private int redPercentage;
    private int greenPercentage;
    private int bluePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.contrastEnhancementProgressBar);
        beforeView = findViewById(R.id.contrastEnhancementImageBefore);
        afterView = findViewById(R.id.contrastEnhancementImageAfter);

        redPercentage = 100;
        greenPercentage = 100;
        bluePercentage = 100;

        redSeekBar = findViewById(R.id.redConstantEqualizationSeekBar);
        greenSeekBar = findViewById(R.id.greenConstantEqualizationSeekBar);
        blueSeekBar = findViewById(R.id.blueConstantEqualizationSeekBar);

        TextView redSeekBarTextView = findViewById(R.id.redConstantEqualizationTextView);
        TextView greenSeekBarTextView = findViewById(R.id.greenConstantEqualizationTextView);
        TextView blueSeekBarTextView = findViewById(R.id.blueConstantEqualizationTextView);

        redSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(redSeekBarTextView));
        greenSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(greenSeekBarTextView));
        blueSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(blueSeekBarTextView));

        try {
            ArrayList<ContrastEnhancementOption> options = JSONSerializer.arrayDeserialize(this, Text.readRawResource(this, R.raw.contrast_enhancement_options), ContrastEnhancementOption.class);
            ContrastEnhancementAdapter adapter = new ContrastEnhancementAdapter(options);

            algorithmSpinner = findViewById(R.id.equalizationAlgorithmSpinner);
            algorithmSpinner.setAdapter(adapter);
            algorithmSpinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }

        enhanceButton = findViewById(R.id.enhanceContrastButton);
        enhanceButton.setOnClickListener(getButtonOnClickListener());
        UI.disable(enhanceButton);

        redGraphView = findViewById(R.id.contrastEnhancementRedGraphView);
        greenGraphView = findViewById(R.id.contrastEnhancementGreenGraphView);
        blueGraphView = findViewById(R.id.contrastEnhancementBlueGraphView);

        UI.hide(redGraphView);
        UI.hide(greenGraphView);
        UI.hide(blueGraphView);

        UI.showAllXGraphView(redGraphView);
        UI.showAllXGraphView(greenGraphView);
        UI.showAllXGraphView(blueGraphView);

        ImageLoadTask imageLoadTask = new ImageLoadTask();
        imageLoadTask.execute(cachedImageDataURI);
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

    // Seek bar on change listener
    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(seekBar == redSeekBar) redPercentage = progress;
                    else if(seekBar == greenSeekBar) greenPercentage = progress;
                    else if(seekBar == blueSeekBar) bluePercentage = progress;

                    textView.setText(Integer.toString(progress) + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    // Spinner on item selected listener
    private AdapterView.OnItemSelectedListener getSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ContrastEnhancementOption selectedOption = (ContrastEnhancementOption) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;
                textView.setText(selectedOption.algorithm + "   ▾");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Button on click listener
    private View.OnClickListener getButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContrastEnhancementTask contrastEnhancementTask = new ContrastEnhancementTask();
                contrastEnhancementTask.execute();
            }
        };
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return !(originalImage.rgb.isDataEmpty()); }
}
