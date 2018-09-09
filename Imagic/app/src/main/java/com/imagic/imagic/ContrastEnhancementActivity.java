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

    // Histogram async task
    private class HistogramGenerationTask extends AsyncTask<Image.ColorType, Integer, Void> {
        @Override
        protected Void doInBackground(Image.ColorType... colorTypes) {
            int numColors = colorTypes.length;
            int done = 0;
            publishProgress(countProgress(done + 1, numColors + 1));

            for(Image.ColorType colorType : colorTypes) {
                originalImage.generateHistogramByColorType(colorType);

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

            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            transformedImage = originalImage;
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

            try {
                Method method = transformedImage.rgb.red.getClass().getSuperclass().getMethod(selectedOption.executeFunctionOnButtonClick, double.class);

                int[] newRedValue = (int[]) method.invoke(transformedImage.rgb.red, (double)(redPercentage)/100);
                done++;
                publishProgress(countProgress(done + 1, numTransformations + 2));

                int[] newGreenValue = (int[]) method.invoke(transformedImage.rgb.green, (double)(greenPercentage)/100);
                done++;
                publishProgress(countProgress(done + 1, numTransformations + 2));

                int[] newBlueValue = (int[]) method.invoke(transformedImage.rgb.blue, (double)(bluePercentage)/100);
                done++;
                publishProgress(countProgress(done + 1, numTransformations + 2));

                transformedImage.updateBitmap(getApplicationContext(), newRedValue, newGreenValue, newBlueValue);
                publishProgress(countProgress(done + 2, numTransformations + 2));
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            transformedImage = new Image(originalImage);

            UI.disable(enhanceButton);
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

            UI.setInvisible(progressBar);
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
    private static Uri cachedImageDataURI;

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
    private Button enhanceButton;

    // SeekBar percentage value
    private int redPercentage;
    private int greenPercentage;
    private int bluePercentage;

    // Selected option
    private ContrastEnhancementOption selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.contrastEnhancementProgressBar);
        beforeView = findViewById(R.id.contrastEnhancementImageBefore);
        afterView = findViewById(R.id.contrastEnhancementImageAfter);

        redSeekBar = findViewById(R.id.redConstantEqualizationSeekBar);
        greenSeekBar = findViewById(R.id.greenConstantEqualizationSeekBar);
        blueSeekBar = findViewById(R.id.blueConstantEqualizationSeekBar);

        TextView redSeekBarTextView = findViewById(R.id.redConstantEqualizationTextView);
        TextView greenSeekBarTextView = findViewById(R.id.greenConstantEqualizationTextView);
        TextView blueSeekBarTextView = findViewById(R.id.blueConstantEqualizationTextView);

        Spinner spinner = findViewById(R.id.equalizationAlgorithmSpinner);
        enhanceButton = findViewById(R.id.enhanceContrastButton);

        redGraphView = findViewById(R.id.contrastEnhancementRedGraphView);
        greenGraphView = findViewById(R.id.contrastEnhancementGreenGraphView);
        blueGraphView = findViewById(R.id.contrastEnhancementBlueGraphView);

        UI.showAllXGraphView(redGraphView);
        UI.showAllXGraphView(greenGraphView);
        UI.showAllXGraphView(blueGraphView);

        try {
            originalImage = JSONSerializer.deserialize(getApplicationContext(), Cache.read(cachedImageDataURI), Image.class);
            transformedImage = new Image(originalImage);

            UI.updateImageView(this, originalImage.uri, beforeView);
            UI.updateImageView(this, transformedImage.uri, afterView);

            redPercentage = 100;
            greenPercentage = 100;
            bluePercentage = 100;

            redSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(redSeekBarTextView));
            greenSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(greenSeekBarTextView));
            blueSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(blueSeekBarTextView));

            ArrayList<ContrastEnhancementOption> options = JSONSerializer.arrayDeserialize(getApplicationContext(), Text.readRawResource(getApplicationContext(), R.raw.contrast_enhancement_options), ContrastEnhancementOption.class);
            ContrastEnhancementAdapter adapter = new ContrastEnhancementAdapter(options);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());

            UI.disable(enhanceButton);
            enhanceButton.setOnClickListener(getButtonOnClickListener());

            if(dataAvailableInCache()) {
                originalImage.rgb.enableValueDependentColor();

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
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }
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
                selectedOption = (ContrastEnhancementOption) adapterView.getItemAtPosition(position);
                String selectedAlgorithm = selectedOption.algorithm;

                TextView textView = (TextView) view;
                textView.setText(selectedAlgorithm + "   ▾");
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
