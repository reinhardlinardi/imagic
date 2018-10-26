package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

public class EqualizerFragment extends Fragment implements MainActivityListener {

    /* Properties */

    // Communicator variable
    private FragmentListener activity;

    // UI components
    private LinearLayout container;
    private ProgressBar progressBar;

    private ImageView imageView;
    private ImageView transformedImageView;

    private TextView helpTextView;
    private Button resetButton;
    private Button applyButton;

    private GraphView userHistogramGraphView;
    private SeekBar colorSeekBar;
    private SeekBar frequencySeekBar;
    private TextView colorTextView;
    private TextView frequencyTextView;

    private Button matchButton;

    private GraphView redGraphView;
    private GraphView transformedRedGraphView;
    private GraphView greenGraphView;
    private GraphView transformedGreenGraphView;
    private GraphView blueGraphView;
    private GraphView transformedBlueGraphView;

    // Seek bar values
    private int[] colorValues;
    private int[] frequencyValues;

    // Current selected data point color array index
    private int selectedIndex;

    // Transformed image and histogram
    Image image;
    RGBHistogram rgb;

    // User defined histogram
    GrayscaleHistogram userHistogram;

    /* Lifecycles */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_equalizer, container, false); }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isAttachedToMainActivity()) {
            container = view.findViewById(R.id.equalizerContainer);

            progressBar = view.findViewById(R.id.equalizerProgressBar);
            helpTextView = view.findViewById(R.id.equalizerHelpTextView);

            userHistogramGraphView = view.findViewById(R.id.equalizerUserHistogramGraphView);
            UI.setGraphViewXAxisBoundary(userHistogramGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewYAxisBoundary(userHistogramGraphView, 0, 300);

            redGraphView = view.findViewById(R.id.equalizerRedGraphView);
            greenGraphView = view.findViewById(R.id.equalizerGreenGraphView);
            blueGraphView = view.findViewById(R.id.equalizerBlueGraphView);
            transformedRedGraphView = view.findViewById(R.id.equalizerTransformedRedGraphView);
            transformedGreenGraphView = view.findViewById(R.id.equalizerTransformedGreenGraphView);
            transformedBlueGraphView = view.findViewById(R.id.equalizerTransformedBlueGraphView);

            UI.setGraphViewXAxisBoundary(redGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(greenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(blueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedRedGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedGreenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedBlueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);

            resetButton = view.findViewById(R.id.equalizerResetButton);
            applyButton = view.findViewById(R.id.equalizerApplyButton);
            resetButton.setOnClickListener(getResetButtonOnClickListener());
            applyButton.setOnClickListener(getApplyButtonOnClickListener());

            colorSeekBar = view.findViewById(R.id.equalizerColorSeekBar);
            frequencySeekBar = view.findViewById(R.id.equalizerFrequencySeekBar);
            colorTextView = view.findViewById(R.id.equalizerColorTextView);
            frequencyTextView = view.findViewById(R.id.equalizerFrequencyTextView);

            resetSeekBar();

            colorSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(colorTextView));
            frequencySeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(frequencyTextView));

            matchButton = view.findViewById(R.id.equalizerMatchButton);
            matchButton.setOnClickListener(getMatchButtonOnClickListener());

            transformedImageView = view.findViewById(R.id.equalizerTransformedImageView);
            imageView = view.findViewById(R.id.equalizerImageView);
            imageView.setOnClickListener(getImageViewOnClickListener());

            generateUserHistogram(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UI.clearMemory(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    /* Implemented interface methods */

    // Send given intent to select image
    public void sendSelectImageIntent(Intent intent) { startActivityForResult(intent, IntentRequestCode.SELECT_IMAGE.code); }

    // Send given intent to capture image
    public void sendCaptureImageIntent(Intent intent) { startActivityForResult(intent, IntentRequestCode.CAPTURE_IMAGE.code); }

    // Load image when fragment is selected
    public void loadImageOnSelected() {
        if(isAttachedToMainActivity()) {
            if(activity.isImageHasBitmap()) {
                ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                imageLoadAsyncTask.execute(false);
            }
        }
    }

    /* Intent result */

    // Change image based on select or capture image activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(isAttachedToMainActivity()) {
            if(activity.onImageIntentResult(requestCode, resultCode, data) && activity.hasImage()) {
                ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                imageLoadAsyncTask.execute(true);
            }
        }
    }

    /* Event listeners */

    // Get image view on click listener
    private View.OnClickListener getImageViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    // Show image selection dialog
                    ImageDialogFragment imageDialog = new ImageDialogFragment();
                    imageDialog.show(getFragmentManager(), ImageDialogFragment.TAG);
                }
            }
        };
    }

    // Get reset button on click listener
    private View.OnClickListener getResetButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    // Reset image
                    if(activity.hasImage()) {
                        resetSeekBar();
                        generateUserHistogram(false);

                        ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                        imageLoadAsyncTask.execute(true);
                    }
                }
            }
        };
    }

    // Get apply button on click listener
    private View.OnClickListener getApplyButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    if(activity.hasImage()) {
                        resetSeekBar();
                        generateUserHistogram(false);
                        activity.updateImage(image);

                        ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                        imageLoadAsyncTask.execute(false);
                    }
                }
            }
        };
    }

    // Get user histogram on data point tap listener
    private OnDataPointTapListener getUserHistogramOnDataPointTapListener() {
        return new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                DataPoint data = (DataPoint) dataPoint;

                for(int idx = 0; idx < 4; idx++) {
                    if(colorValues[idx] == (int) data.getX()) {
                        selectedIndex = idx;
                        break;
                    }
                }

                if(selectedIndex == 0 || selectedIndex == 4) UI.hide(colorSeekBar);
                else {
                    int colorProgress = getSeekBarProgressFromColorValue(colorValues[selectedIndex]);
                    colorSeekBar.setProgress(colorProgress);

                    UI.show(colorSeekBar);
                }

                colorTextView.setText(Integer.toString(colorValues[selectedIndex]));

                int frequencyProgress = getSeekBarProgressFromFrequencyValue(frequencyValues[selectedIndex]);
                frequencySeekBar.setProgress(frequencyProgress);
                frequencyTextView.setText(Integer.toString(frequencyValues[selectedIndex]));
            }
        };
    }

    // Get seek bar on change listener
    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    int value;

                    if(seekBar == colorSeekBar) {
                        value = getColorValueFromSeekBarProgress(progress);
                        colorValues[selectedIndex] = value;
                    }
                    else {
                        value = getFrequencyValueFromSeekBarProgress(progress);
                        frequencyValues[selectedIndex] = value;
                    }

                    textView.setText(Integer.toString(value));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(selectedIndex == 1 && colorValues[1] >= colorValues[2]) swapValues(1, 2);
                else if(selectedIndex == 2 && colorValues[2] <= colorValues[1]) swapValues(2, 1);

                generateUserHistogram(true);
            }
        };
    }

    // Get match button on click listener
    private View.OnClickListener getMatchButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    if(activity.hasImage()) {
                        image = new Image(getContext(), activity.getImage());
                        rgb = new RGBHistogram(activity.getRGBHistogram());

                        HistogramMatchingAsyncTask histogramMatchingAsyncTask = new HistogramMatchingAsyncTask();
                        histogramMatchingAsyncTask.execute();
                    }
                }
            }
        };
    }

    /* Methods */

    // Constructor
    public EqualizerFragment() {}

    // Factory method to create new instance of fragment
    public static EqualizerFragment newInstance() { return new EqualizerFragment(); }

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

    // Reset seek bar
    private void resetSeekBar() {
        colorValues = new int[4];
        frequencyValues = new int[4];

        for(int idx = 0; idx < 4; idx++) {
            colorValues[idx] = idx * ColorHistogram.MAX_VALUE / 3;
            frequencyValues[idx] = 100;
        }

        colorSeekBar.setProgress(0);
        colorTextView.setText(R.string.seek_bar_0_text);

        frequencySeekBar.setProgress(100);
        frequencyTextView.setText(R.string.seek_bar_100_text);
    }

    // Generate user histogram
    private void generateUserHistogram(boolean solveEquation) {
        if(!solveEquation) selectedIndex = 0;

        DataPoint[] pointDataArray = new DataPoint[4];
        for(int idx = 0; idx < 4; idx++) pointDataArray[idx] = new DataPoint(colorValues[idx], frequencyValues[idx]);

        PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<>(pointDataArray);
        pointSeries.setColor(Color.LTGRAY);
        pointSeries.setOnDataPointTapListener(getUserHistogramOnDataPointTapListener());

        int[] seriesDataArray = new int[ColorHistogram.NUM_OF_VALUE];

        if(solveEquation) {
            double[][] coefficients = new double[3][3];
            double[] constants = new double[3];

            for(int row = 0; row < 3; row++) {
                constants[row] = frequencyValues[row + 1] - frequencyValues[0];
                for(int col = 0; col < 3; col++) coefficients[row][col] = Math.pow(colorValues[row + 1], 3 - col);
            }

            PolynomialFunction function = getPolynomialFunction(coefficients, constants);
            for(int idx = ColorHistogram.MIN_VALUE; idx <= ColorHistogram.MAX_VALUE; idx++) seriesDataArray[idx] = Math.max(0, (int)function.compute((double)idx));
        }
        else for(int idx = ColorHistogram.MIN_VALUE; idx <= ColorHistogram.MAX_VALUE; idx++) seriesDataArray[idx] = 100;

        userHistogram = new GrayscaleHistogram();
        userHistogram.setData(seriesDataArray);
        BarGraphSeries<DataPoint> barSeries = userHistogram.getBarGraphSeries();

        UI.setGraphView(userHistogramGraphView, barSeries, pointSeries);
    }

    // Get a third-degree polynomial function (cubic function) from all four points
    private PolynomialFunction getPolynomialFunction(double[][] coefficients, double[] constants) {
        LinearEquation equation = new LinearEquation(3);
        equation.coefficients = coefficients;
        equation.constants = constants;

        PolynomialFunction function = new PolynomialFunction(3, frequencyValues[0]);
        double[] result = equation.solve();
        function.coefficients = result;

        return function;
    }

    // Convert seek bar progress to color value
    private int getColorValueFromSeekBarProgress(int progress) {
        int colorValue = progress;

        if(selectedIndex == 1 || selectedIndex == 2) colorValue += selectedIndex;
        if(selectedIndex == 1 && colorValue >= colorValues[2]) colorValue += 1;
        if(selectedIndex == 2 && colorValue <= colorValues[1]) colorValue -= 1;

        return colorValue;
    }

    // Convert seek bar progress to frequency value
    private int getFrequencyValueFromSeekBarProgress(int progress) { return progress + 1; }

    // Convert color value to seek bar progress
    private int getSeekBarProgressFromColorValue(int value) {
        int seekBarProgress = value;
        if(selectedIndex == 1 || selectedIndex == 2) seekBarProgress -= selectedIndex;

        return seekBarProgress;
    }

    // Convert frequency value to seek bar progress
    private int getSeekBarProgressFromFrequencyValue(int value) { return value - 1; }

    // Swap values and change selected color array index from source index to destination index
    private void swapValues(int srcIdx, int destIdx) {
        int temp = colorValues[srcIdx];
        colorValues[srcIdx] = colorValues[destIdx];
        colorValues[destIdx] = temp;

        temp = frequencyValues[srcIdx];
        frequencyValues[srcIdx] = frequencyValues[destIdx];
        frequencyValues[destIdx] = temp;

        selectedIndex = destIdx;
    }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Get missing histogram data color types
    private ArrayList<ColorType> getMissingHistogramDataColorTypes() {
        if(isAttachedToMainActivity()) {
            ArrayList<ColorType> missingColorTypes = new ArrayList<>();

            if(!activity.isRGBHistogramDataAvailable()) {
                missingColorTypes.add(ColorType.RED);
                missingColorTypes.add(ColorType.GREEN);
                missingColorTypes.add(ColorType.BLUE);
            }

            return missingColorTypes;
        }
        else return new ArrayList<>();
    }

    /* Async tasks */

    // Image load async task
    private class ImageLoadAsyncTask extends AsyncTask<Boolean, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... bools) {
            boolean executedFromIntentResult = bools[0];
            publishProgress(countProgress(1, 2));

            try {
                if(isAttachedToMainActivity() && executedFromIntentResult) activity.loadImageBitmap(imageView.getWidth(), imageView.getHeight());
                publishProgress(countProgress(2, 2));
            } catch (Exception e) {
                Debug.ex(e);
            }

            return executedFromIntentResult;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()) {
                UI.setUnclickable(imageView);
                UI.setUnclickable(userHistogramGraphView);
                UI.disable(resetButton);
                UI.disable(applyButton);

                UI.disable(colorSeekBar);
                UI.disable(frequencySeekBar);
                UI.disable(matchButton);

                if(UI.isVisible(helpTextView)) UI.hide(helpTextView);

                progressBar.setProgress(0);
                UI.show(progressBar);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { if(isAttachedToMainActivity()) progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Boolean executedFromIntentResult) {
            if(isAttachedToMainActivity()) {
                image = new Image(getContext(), activity.getImage());
                rgb = new RGBHistogram(activity.getRGBHistogram());

                UI.setImageView(getContext(), imageView, image.bitmap);
                UI.setImageView(getContext(), transformedImageView, image.bitmap);

                UI.clearMemory(getContext());
                UI.setInvisible(progressBar);

                // If user change image, reset all histogram data
                if(executedFromIntentResult) activity.resetAllHistogramData();
                ArrayList<ColorType> missingColorTypes = getMissingHistogramDataColorTypes();

                // If any histogram data is missing, generate data
                if(!missingColorTypes.isEmpty()) {
                    HistogramGenerationAsyncTask histogramGenerationAsyncTask = new HistogramGenerationAsyncTask();
                    histogramGenerationAsyncTask.execute(missingColorTypes.toArray(new ColorType[missingColorTypes.size()]));
                } else {
                    UI.setGraphView(redGraphView, rgb.red.getBarGraphSeries());
                    UI.setGraphView(greenGraphView, rgb.green.getBarGraphSeries());
                    UI.setGraphView(blueGraphView, rgb.blue.getBarGraphSeries());

                    UI.setGraphView(transformedRedGraphView, rgb.red.getBarGraphSeries());
                    UI.setGraphView(transformedGreenGraphView, rgb.green.getBarGraphSeries());
                    UI.setGraphView(transformedBlueGraphView, rgb.blue.getBarGraphSeries());

                    UI.setClickable(imageView);
                    UI.setClickable(userHistogramGraphView);
                    UI.enable(resetButton);
                    UI.enable(applyButton);

                    UI.enable(colorSeekBar);
                    UI.enable(frequencySeekBar);
                    UI.enable(matchButton);

                    if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                    if(!UI.isVisible(container)) UI.show(container);
                }
            }
        }
    }

    // Histogram generation async task
    private class HistogramGenerationAsyncTask extends AsyncTask<ColorType, Integer, Void> {

        @Override
        protected Void doInBackground(ColorType... colorTypes) {
            int numColors = colorTypes.length;
            int numTask = numColors + 1;
            int taskDone = 0;

            publishProgress(countProgress(++taskDone, numTask));

            for(ColorType colorType : colorTypes) {
                if(isAttachedToMainActivity()) activity.updateHistogramData(colorType);
                publishProgress(countProgress(++taskDone, numTask));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()) {
                progressBar.setProgress(0);
                UI.show(progressBar);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { if(isAttachedToMainActivity()) progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void result) {
            if(isAttachedToMainActivity()) {
                rgb = new RGBHistogram(activity.getRGBHistogram());

                UI.setGraphView(redGraphView, rgb.red.getBarGraphSeries());
                UI.setGraphView(greenGraphView, rgb.green.getBarGraphSeries());
                UI.setGraphView(blueGraphView, rgb.blue.getBarGraphSeries());

                UI.setGraphView(transformedRedGraphView, rgb.red.getBarGraphSeries());
                UI.setGraphView(transformedGreenGraphView, rgb.green.getBarGraphSeries());
                UI.setGraphView(transformedBlueGraphView, rgb.blue.getBarGraphSeries());

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.setClickable(userHistogramGraphView);
                UI.enable(resetButton);
                UI.enable(applyButton);

                UI.enable(colorSeekBar);
                UI.enable(frequencySeekBar);
                UI.enable(matchButton);

                if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                if(!UI.isVisible(container)) UI.show(container);
            }
        }
    }

    // Histogram matching async task
    private class HistogramMatchingAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if(isAttachedToMainActivity()) {
                double[] cdf = userHistogram.getCDF();
                publishProgress(countProgress(1, 3));

                int[][] mapping = rgb.matchHistogram(cdf);
                publishProgress(countProgress(2, 3));

                image.updateBitmapByColorMapping(mapping[0], mapping[1], mapping[2]);
                publishProgress(countProgress(3, 3));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()) {
                UI.setUnclickable(imageView);
                UI.setUnclickable(userHistogramGraphView);
                UI.disable(resetButton);
                UI.disable(applyButton);

                UI.disable(colorSeekBar);
                UI.disable(frequencySeekBar);
                UI.disable(matchButton);

                progressBar.setProgress(0);
                UI.show(progressBar);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { if(isAttachedToMainActivity()) progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void result) {
            if(isAttachedToMainActivity()) {
                UI.setImageView(getContext(), transformedImageView, image.bitmap);
                UI.clearMemory(getContext());

                UI.setGraphView(transformedRedGraphView, rgb.red.getBarGraphSeries());
                UI.setGraphView(transformedGreenGraphView, rgb.green.getBarGraphSeries());
                UI.setGraphView(transformedBlueGraphView, rgb.blue.getBarGraphSeries());

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.setClickable(userHistogramGraphView);
                UI.enable(resetButton);
                UI.enable(applyButton);

                UI.enable(colorSeekBar);
                UI.enable(frequencySeekBar);
                UI.enable(matchButton);
            }
        }
    }
}
