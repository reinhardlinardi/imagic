package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

class Slider {

    // Properties
    public int sliderViewID;
    public int textViewID;
    public SeekBar seekBar;
    public TextView textView;
    public int value;

    // Constructor
    Slider(Activity activity, int sliderViewID, int textViewID) {
        this.sliderViewID = sliderViewID;
        this.textViewID = textViewID;
        seekBar = activity.findViewById(sliderViewID);
        textView = activity.findViewById(textViewID);

        seekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener());
    }

    // Set value
    private void setValue(int value) { this.value = value; }

    // Update text view value
    @SuppressLint("SetTextI18n")
    private void updateTextViewValue() { textView.setText(Integer.toString(value) + "%"); }

    // On seek bar change listener function
    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    setValue(progress);
                    updateTextViewValue();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }
}
