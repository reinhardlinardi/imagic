package com.imagic.imagic;

import android.net.Uri;
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

import java.util.ArrayList;

public class ContrastEnhancementActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.contrastEnhancementProgressBar);
        beforeView = findViewById(R.id.contrastEnhancementImageBefore);
        afterView = findViewById(R.id.contrastEnhancementImageAfter);

        SeekBar redSeekBar = findViewById(R.id.redConstantEqualizationSeekBar);
        SeekBar greenSeekBar = findViewById(R.id.greenConstantEqualizationSeekBar);
        SeekBar blueSeekBar = findViewById(R.id.blueConstantEqualizationSeekBar);

        TextView redSeekBarTextView = findViewById(R.id.redConstantEqualizationTextView);
        TextView greenSeekBarTextView = findViewById(R.id.greenConstantEqualizationTextView);
        TextView blueSeekBarTextView = findViewById(R.id.blueConstantEqualizationTextView);

        Spinner spinner = findViewById(R.id.equalizationAlgorithmSpinner);
        Button button = findViewById(R.id.enhanceContrastButton);

        redGraphView = findViewById(R.id.contrastEnhancementRedGraphView);
        greenGraphView = findViewById(R.id.contrastEnhancementGreenGraphView);
        blueGraphView = findViewById(R.id.contrastEnhancementBlueGraphView);

        UI.showAllXGraphView(redGraphView);
        UI.showAllXGraphView(greenGraphView);
        UI.showAllXGraphView(blueGraphView);

        UI.hide(redGraphView);
        UI.hide(greenGraphView);
        UI.hide(blueGraphView);

        try {
            originalImage = JSONSerializer.deserialize(getApplicationContext(), Cache.read(cachedImageDataURI), Image.class);
            transformedImage = originalImage;

            UI.updateImageView(this, originalImage.uri, beforeView);
            UI.updateImageView(this, originalImage.uri, afterView);

            redSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(redSeekBarTextView));
            greenSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(greenSeekBarTextView));
            blueSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(blueSeekBarTextView));

            ArrayList<ContrastEnhancementOption> options = JSONSerializer.arrayDeserialize(getApplicationContext(), Text.readRawResource(getApplicationContext(), R.raw.contrast_enhancement_options), ContrastEnhancementOption.class);
            ContrastEnhancementAdapter adapter = new ContrastEnhancementAdapter(options);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());

            // Button on click listener

            /*
            if(dataAvailableInCache()) {
                transformedImage.rgb.enableValueDependentColor();


            }
            else {

            }
            */
            /*
            StringBuilder imageData = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) imageData.append(line);
            String json = imageData.toString();

            originalImage = new Image();
            transformedImage = new Image();

            originalImage.jsonDeserialize(this, json);
            transformedImage.jsonDeserialize(this, json);

            if(!dataAvailableInCache()) Log.d("Cache", "Data not available");

            redSlider = new Slider(this, R.id.redConstantEqualizationSeekBar, R.id.redConstantEqualizationTextView);
            greenSlider = new Slider(this, R.id.greenConstantEqualizationSeekBar, R.id.greenConstantEqualizationTextView);
            blueSlider = new Slider(this, R.id.blueConstantEqualizationSeekBar, R.id.blueConstantEqualizationTextView);

            progressBar = new Progress(this, R.id.contrastEnhancementProgressBar);

            beforeView = findViewById(R.id.contrastEnhancementImageBefore);
            afterView = findViewById(R.id.contrastEnhancementImageAfter);

            transformedImage.redHistogram.view = findViewById(R.id.contrastEnhancementRedGraphView);
            transformedImage.greenHistogram.view = findViewById(R.id.contrastEnhancementGreenGraphView);
            transformedImage.blueHistogram.view = findViewById(R.id.contrastEnhancementBlueGraphView);
            transformedImage.redHistogram.view.getViewport().setMinX(0f);
            transformedImage.redHistogram.view.getViewport().setMaxX((double) 255);
            transformedImage.redHistogram.view.getViewport().setXAxisBoundsManual(true);

            transformedImage.greenHistogram.view.getViewport().setMinX(0f);
            transformedImage.greenHistogram.view.getViewport().setMaxX((double) 255);
            transformedImage.greenHistogram.view.getViewport().setXAxisBoundsManual(true);

            transformedImage.blueHistogram.view.getViewport().setMinX(0f);
            transformedImage.blueHistogram.view.getViewport().setMaxX((double) 255);
            transformedImage.blueHistogram.view.getViewport().setXAxisBoundsManual(true);

            transformedImage.redHistogram.hide();
            transformedImage.greenHistogram.hide();
            transformedImage.blueHistogram.hide();

            Glide.with(this).load(originalImage.bitmap).into(beforeView);
            Glide.with(this).load(transformedImage.bitmap).into(afterView);

            /*
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedText = adapterView.getItemAtPosition(i).toString();
                    ((TextView) view).setText(selectedText + "    ▾");
                    /*
                    spinnerText = adapterView.getItemAtPosition(i).toString();
                    transformedImage.bitmap = originalImage.bitmap.copy(Bitmap.Config.ARGB_8888,true);

                    if(spinnerText.equals("Stretching")) {
                        transformedImage.redHistogram.linearHistogram(originalImage.redHistogram.dataPoints);
                        transformedImage.greenHistogram.linearHistogram(originalImage.greenHistogram.dataPoints);
                        transformedImage.blueHistogram.linearHistogram(originalImage.blueHistogram.dataPoints);

                        Log.d("Enter", "Stretching");
                    }
                    else if(spinnerText.equals("CDF")) {
                        transformedImage.redHistogram.cummulativeEqualizeHistogram(originalImage.redHistogram.dataPoints);
                        transformedImage.greenHistogram.cummulativeEqualizeHistogram(originalImage.greenHistogram.dataPoints);
                        transformedImage.blueHistogram.cummulativeEqualizeHistogram(originalImage.blueHistogram.dataPoints);

                        Log.d("Enter", "CDF");
                    }
                    else {
                        transformedImage.redHistogram.logarithmicHistogram(originalImage.redHistogram.dataPoints);
                        transformedImage.greenHistogram.logarithmicHistogram(originalImage.greenHistogram.dataPoints);
                        transformedImage.blueHistogram.logarithmicHistogram(originalImage.blueHistogram.dataPoints);

                        Log.d("Enter", "Log");
                    }
                    transformedImage.updateBitmap();

                    transformedImage.redHistogram.enableValueDependentColor();
                    transformedImage.greenHistogram.enableValueDependentColor();
                    transformedImage.blueHistogram.enableValueDependentColor();

                    transformedImage.redHistogram.show();
                    transformedImage.greenHistogram.show();
                    transformedImage.blueHistogram.show();

                    transformedImage.redHistogram.render();
                    transformedImage.greenHistogram.render();
                    transformedImage.blueHistogram.render();

                    Glide.with(ContrastEnhancementActivity.this).load(transformedImage.bitmap).into(afterView);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            */
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

    // Seek bar on change listener
    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) textView.setText(Integer.toString(progress) + "%");
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
                String selectedAlgorithm = selectedOption.algorithm;

                TextView textView = (TextView) view;
                textView.setText(selectedAlgorithm + "   ▾");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return !(originalImage.rgb.isDataEmpty()); }

    // Show toast on task completion
    private void showToastOnTaskCompletion() {
        Toast.makeText(this, "Enhancement finished.", Toast.LENGTH_SHORT).show();
    }
}
