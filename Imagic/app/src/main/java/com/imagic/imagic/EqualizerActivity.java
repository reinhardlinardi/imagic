package com.imagic.imagic;

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

    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Implement plz
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
}
