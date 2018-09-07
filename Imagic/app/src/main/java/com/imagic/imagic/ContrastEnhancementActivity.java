package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ContrastEnhancementActivity extends AppCompatActivity {

    // Dropdown option
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
        public void jsonDeserialize(Activity activity, String json) throws Exception {
            JSONObject optionJSON = new JSONObject(json);

            name = optionJSON.getString("name");
            functionToExecute = optionJSON.getString("functionToExecute");
        }
    }

    // Spinner adapter
    private class SpinnerAdapter extends ArrayAdapter<ContrastEnhancementActivity.Option> {

        SpinnerAdapter(ArrayList<ContrastEnhancementActivity.Option> options) {
            super(ContrastEnhancementActivity.this, R.layout.contrast_enhance_spinner_option, options);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ContrastEnhancementActivity.this.getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View optionView = inflater.inflate(R.layout.contrast_enhance_spinner_option, null, true);

            TextView optionTextView = optionView.findViewById(R.id.contrastEnhanceSpinnerOptionTextView);
            ContrastEnhancementActivity.Option option = getItem(position);

            if(option != null) optionTextView.setText(option.name);
            return optionView;
        }
    }

    // Internal shared cached image data URI
    private static Uri imageDataURI;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            ContrastEnhancementActivity.imageDataURI = Uri.parse(bundle.getString("imageData"));
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

                Glide.with(this).load(originalImage.bitmap).into(beforeView);
                Glide.with(this).load(transformedImage.bitmap).into(afterView);

//                Image.ColorType[] colorTypes = Image.ColorType.values();
//
//                for(Image.ColorType colorType : colorTypes) {
//                    ContrastEnhancementActivity.this.transformedImage.generateHistogramByColorType(colorType);
//                }

                //ArrayList<ContrastEnhancementActivity.Option> options = new ArrayList<>();
                //JSONSerializer.deserialize(this, Text.readRawResource(this, R.raw.equalization_algorithms), (ArrayList<JSONSerializable>) options);
                /*
                JSONArray optionList = new JSONArray(Text.readRawResource(this, R.raw.equalization_algorithms));

                for(int idx = 0; idx < optionList.length(); idx++) {
                    ContrastEnhancementActivity.Option option = new ContrastEnhancementActivity.Option();
                    option.jsonDeserialize(this, optionList.get(idx).toString());
                    options.add(option);
                }

                ContrastEnhancementActivity.SpinnerAdapter spinnerAdapter = new ContrastEnhancementActivity.SpinnerAdapter(options);
                */

                ArrayList<String> options = new ArrayList<>();
                options.add("Stretching");
                options.add("CDF");
                options.add("Logarithmic");

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.contrast_enhance_spinner_option, options);
                Spinner spinner = findViewById(R.id.equalizationAlgorithmSpinner);
                spinner.setAdapter(spinnerAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerText = adapterView.getItemAtPosition(i).toString();
                        transformedImage.bitmap = originalImage.bitmap.copy(Bitmap.Config.ARGB_8888,true);
                        if(adapterView.getItemAtPosition(i).toString().equals("Stretching")) {
                            transformedImage.redHistogram.linearHistogram();
                            transformedImage.greenHistogram.linearHistogram();
                            transformedImage.blueHistogram.linearHistogram();
                        } else if(adapterView.getItemAtPosition(i).toString().equals("CDF")) {
                            transformedImage.redHistogram.cummulativeEqualizeHistogram();
                            transformedImage.greenHistogram.cummulativeEqualizeHistogram();
                            transformedImage.blueHistogram.cummulativeEqualizeHistogram();
                        } else {
                            transformedImage.redHistogram.logarithmicHistogram();
                            transformedImage.greenHistogram.logarithmicHistogram();
                            transformedImage.blueHistogram.logarithmicHistogram();
                        }
                        transformedImage.updateBitmap();
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
        }
    }

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
}
