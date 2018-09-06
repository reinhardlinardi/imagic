package com.imagic.imagic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class ContrastEnhancementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrast_enhancement);

        Slider redSlider = new Slider(this, R.id.redConstantEqualizationSeekBar, R.id.redConstantEqualizationTextView);
        Slider greenSlider = new Slider(this, R.id.greenConstantEqualizationSeekBar, R.id.greenConstantEqualizationTextView);
        Slider blueSlider = new Slider(this, R.id.blueConstantEqualizationSeekBar, R.id.blueConstantEqualizationTextView);
    }
}
