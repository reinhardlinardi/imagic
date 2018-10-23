package com.imagic.imagic;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class HistogramFragment extends Fragment implements MainActivityListener {

    /* Properties */

    // Communicator variable
    private FragmentListener activity;

    // UI components
    private LinearLayout container;

    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView helpTextView;
    private Button resetButton;

    private GraphView redGraphView;
    private GraphView greenGraphView;
    private GraphView blueGraphView;
    private GraphView grayscaleGraphView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_histogram, container, false); }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isAttachedToMainActivity()) {
            container = view.findViewById(R.id.histogramContainer);

            progressBar = view.findViewById(R.id.histogramProgressBar);
            helpTextView = view.findViewById(R.id.histogramHelpTextView);

            redGraphView = view.findViewById(R.id.histogramRedGraphView);
            greenGraphView = view.findViewById(R.id.histogramGreenGraphView);
            blueGraphView = view.findViewById(R.id.histogramBlueGraphView);
            grayscaleGraphView = view.findViewById(R.id.histogramGrayscaleGraphView);

            UI.setGraphViewXAxisBoundary(redGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(greenGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(blueGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);
            UI.setGraphViewXAxisBoundary(grayscaleGraphView, ColorHistogram.MIN_VALUE, ColorHistogram.MAX_VALUE);

            resetButton = view.findViewById(R.id.histogramResetButton);
            resetButton.setOnClickListener(getResetButtonOnClickListener());

            imageView = view.findViewById(R.id.histogramImageView);
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
                        ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                        imageLoadAsyncTask.execute(true);
                    }
                }
            }
        };
    }

    /* Methods */

    // Constructor
    public HistogramFragment() {}

    // Factory method to create new instance of fragment
    public static HistogramFragment newInstance() { return new HistogramFragment(); }

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

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

            if(!activity.isGrayscaleHistogramDataAvailable()) missingColorTypes.add(ColorType.GRAYSCALE);
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
            }
            catch(Exception e) {
                Debug.ex(e);
            }

            return executedFromIntentResult;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()) {
                UI.setUnclickable(imageView);
                UI.disable(resetButton);

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
                UI.clearMemory(getContext());
                UI.setInvisible(progressBar);

                // If user change image, reset all histogram data
                if(executedFromIntentResult) activity.resetAllHistogramData();
                ArrayList<ColorType> missingColorTypes = getMissingHistogramDataColorTypes();

                // If any histogram data is missing, generate data
                if(!missingColorTypes.isEmpty()) {
                    HistogramGenerationAsyncTask histogramGenerationAsyncTask = new HistogramGenerationAsyncTask();
                    histogramGenerationAsyncTask.execute(missingColorTypes.toArray(new ColorType[missingColorTypes.size()]));
                }
                else {
                    UI.setGraphView(redGraphView, activity.getHistogramBarGraphSeriesData(ColorType.RED));
                    UI.setGraphView(greenGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GREEN));
                    UI.setGraphView(blueGraphView, activity.getHistogramBarGraphSeriesData(ColorType.BLUE));
                    UI.setGraphView(grayscaleGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GRAYSCALE));

                    UI.setClickable(imageView);
                    UI.enable(resetButton);

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
                UI.setGraphView(grayscaleGraphView, activity.getHistogramBarGraphSeriesData(ColorType.GRAYSCALE));

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.enable(resetButton);

                if(!UI.isVisible(container)) UI.show(container);
            }
        }
    }
}
