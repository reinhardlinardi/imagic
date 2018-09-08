package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ContrastEnhancementActivity extends AppCompatActivity {

    // Dropdown option
    /*
    private class Option implements JSONSerializable {

        // Properties
        public String name;
        public String functionToExecute;

        // Constructor
        Option() {}

        @Override
        public String jsonSerialize() throws Exception {
            JSONObject optionJSON = new JSONObject();

            optionJSON.put("name", name);
            optionJSON.put("functionToExecute", functionToExecute);

            return optionJSON.toString();
        }

        @Override
        public void jsonDeserialize(Context context, String json) throws Exception {
            JSONObject optionJSON = new JSONObject(json);

            name = optionJSON.getString("name");
            functionToExecute = optionJSON.getString("functionToExecute");
        }
    }
    */
    // Cached image data URI
    private static Uri cachedImageDataURI;

    /*
    // Image bitmap
    private Image originalImage;
    private Image transformedImage;

    // Image view
    private ImageView beforeView;
    private ImageView afterView;

    // Progress bar
    private Progress progressBar;

    // Slider
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;

    // Spinner selected text
    private String spinnerText;
    private Spinner spinner;
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));
        /*
        File imageDataFile = new File(ContrastEnhancementActivity.imageDataURI.getPath());

        try(BufferedReader reader = new BufferedReader(new FileReader(imageDataFile))) {
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

            ArrayList<String> options = new ArrayList<>();
            options.add("CDF");
            options.add("Stretching");
            options.add("Logarithmic");

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.contrast_enhance_spinner_option, options);
            Spinner spinner = findViewById(R.id.equalizationAlgorithmSpinner);
            spinner.setAdapter(spinnerAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
        }
        catch(Exception e) {
                Log.e("Imagic", "Exception", e);
        }
        */
    }
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() {
        if(originalImage.redHistogram.isUninitialized() || originalImage.greenHistogram.isUninitialized() || originalImage.blueHistogram.isUninitialized()) return false;
        else return true;
    }
    */
}
