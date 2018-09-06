package com.imagic.imagic;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ContrastEnhancementActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            ContrastEnhancementActivity.imageDataURI = Uri.parse(bundle.getString("imageData"));
            File imageDataFile = new File(ContrastEnhancementActivity.imageDataURI.getPath());

            try (BufferedReader reader = new BufferedReader(new FileReader(imageDataFile))) {
                StringBuilder imageData = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) imageData.append(line);
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
