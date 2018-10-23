package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class ContrastEnhancementFragment extends Fragment implements MainActivityListener {

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

    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView redTextView;
    private TextView greenTextView;
    private TextView blueTextView;

    private RelativeLayout spinnerContainer;
    private Spinner spinner;
    private Button enhanceButton;

    private GraphView redGraphView;
    private GraphView transformedRedGraphView;
    private GraphView greenGraphView;
    private GraphView transformedGreenGraphView;
    private GraphView blueGraphView;
    private GraphView transformedBlueGraphView;

    // SeekBar percentage value
    private int redPercentage;
    private int greenPercentage;
    private int bluePercentage;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_contrast_enhancement, container, false); }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isAttachedToMainActivity()) {
            container = view.findViewById(R.id.contrastContainer);

            progressBar = view.findViewById(R.id.contrastProgressBar);
            helpTextView = view.findViewById(R.id.contrastHelpTextView);

            redGraphView = view.findViewById(R.id.contrastRedGraphView);
            greenGraphView = view.findViewById(R.id.contrastGreenGraphView);
            blueGraphView = view.findViewById(R.id.contrastBlueGraphView);
            transformedRedGraphView = view.findViewById(R.id.contrastTransformedRedGraphView);
            transformedGreenGraphView = view.findViewById(R.id.contrastTransformedGreenGraphView);
            transformedBlueGraphView = view.findViewById(R.id.contrastTransformedBlueGraphView);

            UI.setGraphViewXAxisBoundary(redGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(greenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(blueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedRedGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedGreenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(transformedBlueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);

            resetButton = view.findViewById(R.id.contrastResetButton);
            applyButton = view.findViewById(R.id.contrastApplyButton);
            resetButton.setOnClickListener(getResetButtonOnClickListener());
            applyButton.setOnClickListener(getApplyButtonOnClickListener());

            redSeekBar = view.findViewById(R.id.contrastRedSeekBar);
            greenSeekBar = view.findViewById(R.id.contrastGreenSeekBar);
            blueSeekBar = view.findViewById(R.id.contrastBlueSeekBar);
            redTextView = view.findViewById(R.id.contrastRedTextView);
            greenTextView = view.findViewById(R.id.contrastGreenTextView);
            blueTextView = view.findViewById(R.id.contrastBlueTextView);

            redPercentage = 100;
            greenPercentage = 100;
            bluePercentage = 100;
            redSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(redTextView));
            greenSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(greenTextView));
            blueSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(blueTextView));

            try {
                String algorithmsJSON = TextFile.readRawResourceFile(getContext(), R.raw.contrast_enhancement_algorithms);
                ArrayList<ContrastEnhancementAlgorithm> algorithms = JSONSerializer.arrayListDeserialize(algorithmsJSON, ContrastEnhancementAlgorithm.class);
                AlgorithmSpinnerAdapter adapter = new AlgorithmSpinnerAdapter(algorithms);

                spinnerContainer = view.findViewById(R.id.contrastSpinnerContainer);
                spinner = view.findViewById(R.id.contrastSpinner);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());
            }
            catch(Exception e) {
                Debug.ex(e);
            }

            enhanceButton = view.findViewById(R.id.contrastEnhanceButton);
            enhanceButton.setOnClickListener(getEnhanceButtonOnClickListener());

            transformedImageView = view.findViewById(R.id.contrastTransformedImageView);
            imageView = view.findViewById(R.id.contrastImageView);
            imageView.setOnClickListener(getImageViewOnClickListener());
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

                        UI.setImageView(getContext(), imageView, activity.getImageBitmap());
                        UI.clearMemory(getContext());
                    }
                }
            }
        };
    }

    // Get seek bar on change listener
    private SeekBar.OnSeekBarChangeListener getSeekBarOnChangeListener(final TextView textView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(seekBar == redSeekBar) redPercentage = progress;
                    else if(seekBar == greenSeekBar) greenPercentage = progress;
                    else if(seekBar == blueSeekBar) bluePercentage = progress;

                    String percentage = Integer.toString(progress) + "%";
                    textView.setText(percentage);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    // Get spinner on item selected listener
    private AdapterView.OnItemSelectedListener getSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ContrastEnhancementAlgorithm algorithm = (ContrastEnhancementAlgorithm) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;

                if(textView != null) textView.setText(algorithm.algorithmName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Get enhance button on click listener
    private View.OnClickListener getEnhanceButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    /* Adapters */

    // Contrast enhancement spinner adapter
    private class AlgorithmSpinnerAdapter extends ArrayAdapter<ContrastEnhancementAlgorithm> {

        /* Methods */

        // Constructor
        AlgorithmSpinnerAdapter(ArrayList<ContrastEnhancementAlgorithm> algorithms) {
            super(ContrastEnhancementFragment.this.getContext(), R.layout.contrast_enhancement_spinner_option, algorithms);
        }

        // Get view
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = ContrastEnhancementFragment.this.getActivity().getLayoutInflater();
            View optionView = inflater.inflate(R.layout.contrast_enhancement_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.contrastSpinnerOptionTextView);
            ContrastEnhancementAlgorithm algorithm = getItem(position);

            if(algorithm != null) optionTextView.setText(algorithm.algorithmName);
            return optionView;
        }

        // Get dropdown view
        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) { return getView(position, view, parent); }
    }

    /* Methods */

    // Constructor
    public ContrastEnhancementFragment() {}

    // Factory method to create new instance of fragment
    public static ContrastEnhancementFragment newInstance() { return new ContrastEnhancementFragment(); }

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

    // Reset seek bar
    private void resetSeekBar() {
        redPercentage = 100;
        greenPercentage = 100;
        bluePercentage = 100;

        redSeekBar.setProgress(100);
        greenSeekBar.setProgress(100);
        blueSeekBar.setProgress(100);

        redTextView.setText("100%");
        greenTextView.setText("100%");
        blueTextView.setText("100%");
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
                UI.disable(resetButton);
                UI.disable(applyButton);

                UI.disable(redSeekBar);
                UI.disable(greenSeekBar);
                UI.disable(blueSeekBar);

                UI.disable(spinnerContainer);
                UI.disable(enhanceButton);

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
                UI.setImageView(getContext(), imageView, activity.getImageBitmap());
                UI.setImageView(getContext(), transformedImageView, activity.getImageBitmap());
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
                    UI.setGraphView(redGraphView, activity.getHistogramBarGraphSeriesData(ColorType.RED));
                    UI.setGraphView(greenGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GREEN));
                    UI.setGraphView(blueGraphView, activity.getHistogramBarGraphSeriesData(ColorType.BLUE));

                    UI.setGraphView(transformedRedGraphView, activity.getHistogramBarGraphSeriesData(ColorType.RED));
                    UI.setGraphView(transformedGreenGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GREEN));
                    UI.setGraphView(transformedBlueGraphView, activity.getHistogramBarGraphSeriesData(ColorType.BLUE));

                    UI.setClickable(imageView);
                    UI.enable(resetButton);
                    UI.enable(applyButton);

                    UI.enable(redSeekBar);
                    UI.enable(greenSeekBar);
                    UI.enable(blueSeekBar);

                    UI.enable(spinnerContainer);
                    UI.enable(enhanceButton);

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
                UI.setGraphView(redGraphView, activity.getHistogramBarGraphSeriesData(ColorType.RED));
                UI.setGraphView(greenGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GREEN));
                UI.setGraphView(blueGraphView, activity.getHistogramBarGraphSeriesData(ColorType.BLUE));

                UI.setGraphView(transformedRedGraphView, activity.getHistogramBarGraphSeriesData(ColorType.RED));
                UI.setGraphView(transformedGreenGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GREEN));
                UI.setGraphView(transformedBlueGraphView, activity.getHistogramBarGraphSeriesData(ColorType.BLUE));

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.enable(resetButton);
                UI.enable(applyButton);

                UI.enable(redSeekBar);
                UI.enable(greenSeekBar);
                UI.enable(blueSeekBar);

                UI.enable(spinnerContainer);
                UI.enable(enhanceButton);

                if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                if(!UI.isVisible(container)) UI.show(container);
            }
        }
    }
}
