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

import java.util.ArrayList;

public class OCRFragment extends Fragment implements MainActivityListener {

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

    private SeekBar thresholdSeekBar;
    private TextView thresholdTextView;

    private RelativeLayout spinnerContainer;
    private Spinner spinner;
    private Button analyzeButton;

    // SeekBar percentage value
    private int thresholdValue;

    // Transformed image and histogram
    Image image;
    RGBHistogram rgb;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_ocr, container, false); }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isAttachedToMainActivity()) {
            container = view.findViewById(R.id.ocrContainer);

            progressBar = view.findViewById(R.id.ocrProgressBar);
            helpTextView = view.findViewById(R.id.ocrHelpTextView);

            resetButton = view.findViewById(R.id.ocrResetButton);
            applyButton = view.findViewById(R.id.ocrApplyButton);
            resetButton.setOnClickListener(getResetButtonOnClickListener());
            applyButton.setOnClickListener(getApplyButtonOnClickListener());

            thresholdSeekBar = view.findViewById(R.id.ocrThresholdSeekBar);
            thresholdTextView = view.findViewById(R.id.ocrThresholdTextView);

            resetSeekBar();
            thresholdSeekBar.setOnSeekBarChangeListener(getSeekBarOnChangeListener(thresholdTextView));

            try {
                String methodsJSON = TextFile.readRawResourceFile(getContext(), R.raw.ocr_methods);
                ArrayList<OCRMethod> methods = JSONSerializer.arrayListDeserialize(methodsJSON, OCRMethod.class);
                MethodSpinnerAdapter adapter = new MethodSpinnerAdapter(methods);

                spinnerContainer = view.findViewById(R.id.ocrSpinnerContainer);
                spinner = view.findViewById(R.id.ocrSpinner);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());
            }
            catch(Exception e) {
                Debug.ex(e);
            }

            analyzeButton = view.findViewById(R.id.ocrAnalyzeButton);
            analyzeButton.setOnClickListener(getAnalyzeButtonOnClickListener());

            transformedImageView = view.findViewById(R.id.ocrTransformedImageView);
            imageView = view.findViewById(R.id.ocrImageView);
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
                        activity.updateImage(image);

                        ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                        imageLoadAsyncTask.execute(false);
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
                    thresholdValue = progress + 1;

                    String value = Integer.toString(progress + 1) + " or lower";
                    textView.setText(value);
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

                OCRMethod method = (OCRMethod) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;

                if(textView != null) textView.setText(method.method);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Get analyze button on click listener
    private View.OnClickListener getAnalyzeButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    if(activity.hasImage()) {
                        image = new Image(getContext(), activity.getImage());
                        rgb = new RGBHistogram(activity.getRGBHistogram());
                        /*
                        ContrastEnhancementAsyncTask contrastEnhancementAsyncTask = new ContrastEnhancementAsyncTask();
                        contrastEnhancementAsyncTask.execute();
                        */
                    }
                }
            }
        };
    }

    /* Adapters */

    // OCR spinner adapter
    private class MethodSpinnerAdapter extends ArrayAdapter<OCRMethod> {

        /* Methods */

        // Constructor
        MethodSpinnerAdapter(ArrayList<OCRMethod> methods) {
            super(OCRFragment.this.getContext(), R.layout.ocr_spinner_option, methods);
        }

        // Get view
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = OCRFragment.this.getActivity().getLayoutInflater();
            View optionView = inflater.inflate(R.layout.ocr_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.ocrSpinnerOptionTextView);
            OCRMethod method = getItem(position);

            if(method != null) optionTextView.setText(method.method);
            return optionView;
        }

        // Get dropdown view
        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) { return getView(position, view, parent); }
    }

    /* Methods */

    // Constructor
    public OCRFragment() {}

    // Factory method to create new instance of fragment
    public static OCRFragment newInstance() { return new OCRFragment(); }

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

    // Reset seek bar
    private void resetSeekBar() {
        thresholdValue = 128;
        thresholdSeekBar.setProgress(127);
        thresholdTextView.setText(R.string.threshold_less_equal_seek_bar_128_text);
    }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
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

                UI.disable(thresholdSeekBar);
                UI.disable(spinnerContainer);
                UI.disable(analyzeButton);

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

                UI.setClickable(imageView);
                UI.enable(resetButton);
                UI.enable(applyButton);

                UI.enable(thresholdSeekBar);
                UI.enable(spinnerContainer);
                UI.enable(analyzeButton);

                if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                if(!UI.isVisible(container)) UI.show(container);
            }
        }
    }
}
