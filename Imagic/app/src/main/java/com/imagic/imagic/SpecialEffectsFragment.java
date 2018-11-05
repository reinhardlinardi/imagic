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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SpecialEffectsFragment extends Fragment implements MainActivityListener {

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

    private RelativeLayout effectSpinnerContainer;
    private RelativeLayout algorithmSpinnerContainer;

    private Spinner effectSpinner;
    private Spinner algorithmSpinner;
    private Button applyEffectButton;

    private LinearLayout kernelContainer;
    private EditText[][] kernel;

    // Transformed image and histogram
    Image image;
    RGBHistogram rgb;

    // ArrayList of adapters for right spinner
    ArrayList<AlgorithmSpinnerAdapter> algorithmAdapters;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return inflater.inflate(R.layout.fragment_special_effects, container, false); }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(isAttachedToMainActivity()) {
            container = view.findViewById(R.id.specialEffectsContainer);

            progressBar = view.findViewById(R.id.specialEffectsProgressBar);
            helpTextView = view.findViewById(R.id.specialEffectsHelpTextView);

            resetButton = view.findViewById(R.id.specialEffectsResetButton);
            applyButton = view.findViewById(R.id.specialEffectsApplyButton);
            resetButton.setOnClickListener(getResetButtonOnClickListener());
            applyButton.setOnClickListener(getApplyButtonOnClickListener());

            try {
                algorithmAdapters = new ArrayList<>();

                String effectsJSON = TextFile.readRawResourceFile(getContext(), R.raw.special_effects);
                ArrayList<SpecialEffect> effects = JSONSerializer.arrayListDeserialize(effectsJSON, SpecialEffect.class);
                EffectSpinnerAdapter effectAdapter = new EffectSpinnerAdapter(effects);

                effectSpinnerContainer = view.findViewById(R.id.specialEffectsEffectSpinnerContainer);
                algorithmSpinnerContainer = view.findViewById(R.id.specialEffectsAlgorithmSpinnerContainer);

                effectSpinner = view.findViewById(R.id.specialEffectsEffectSpinner);
                algorithmSpinner = view.findViewById(R.id.specialEffectsAlgorithmSpinner);

                for(SpecialEffect effect : effects) {
                    ArrayList<SpecialEffectAlgorithm> algorithms = effect.algorithms;
                    AlgorithmSpinnerAdapter algorithmAdapter = new AlgorithmSpinnerAdapter(algorithms);

                    algorithmAdapters.add(algorithmAdapter);
                }

                effectSpinner.setAdapter(effectAdapter);
                algorithmSpinner.setAdapter(algorithmAdapters.get(0));

                effectSpinner.setOnItemSelectedListener(getEffectSpinnerOnItemSelectedListener());
                algorithmSpinner.setOnItemSelectedListener(getAlgorithmSpinnerOnItemSelectedListener());
            }
            catch(Exception e) {
                Debug.ex(e);
            }

            kernelContainer = view.findViewById(R.id.specialEffectsCustomKernelContainer);
            kernel = new EditText[3][3];

            kernel[0][0] = view.findViewById(R.id.specialEffectsEditTextTopLeft);
            kernel[0][1] = view.findViewById(R.id.specialEffectsEditTextTopCenter);
            kernel[0][2] = view.findViewById(R.id.specialEffectsEditTextTopRight);
            kernel[1][0] = view.findViewById(R.id.specialEffectsEditTextMiddleLeft);
            kernel[1][1] = view.findViewById(R.id.specialEffectsEditTextCenter);
            kernel[1][2] = view.findViewById(R.id.specialEffectsEditTextMiddleRight);
            kernel[2][0] = view.findViewById(R.id.specialEffectsEditTextBottomLeft);
            kernel[2][1] = view.findViewById(R.id.specialEffectsEditTextBottomCenter);
            kernel[2][2] = view.findViewById(R.id.specialEffectsEditTextBottomRight);

            resetKernel();

            applyEffectButton = view.findViewById(R.id.specialEffectsApplyEffectButton);
            applyEffectButton.setOnClickListener(getApplyEffectButtonOnClickListener());

            transformedImageView = view.findViewById(R.id.specialEffectsTransformedImageView);
            imageView = view.findViewById(R.id.specialEffectsImageView);
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
                        resetKernel();

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
                        resetKernel();
                        activity.updateImage(image);

                        ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask();
                        imageLoadAsyncTask.execute(false);
                    }
                }
            }
        };
    }

    // Get effect spinner on item selected listener
    private AdapterView.OnItemSelectedListener getEffectSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SpecialEffect effect = (SpecialEffect) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;

                algorithmSpinner.setAdapter(algorithmAdapters.get(position));
                if(textView != null) textView.setText(effect.effect);

                if(effect.effect.equals("Custom")) UI.show(kernelContainer);
                else UI.hide(kernelContainer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Get algorithm spinner on item selected listener
    private AdapterView.OnItemSelectedListener getAlgorithmSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SpecialEffectAlgorithm algorithm = (SpecialEffectAlgorithm) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;

                if(textView != null) textView.setText(algorithm.algorithm);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Get apply effect button on click listener
    private View.OnClickListener getApplyEffectButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAttachedToMainActivity()) {
                    if(activity.hasImage()) {
                        image = new Image(getContext(), activity.getImage());
                        rgb = new RGBHistogram(activity.getRGBHistogram());

                        ConvolutionAsyncTask convolutionAsyncTask = new ConvolutionAsyncTask();
                        convolutionAsyncTask.execute();
                    }
                }
            }
        };
    }

    /* Adapters */

    // Special effect spinner adapter
    private class EffectSpinnerAdapter extends ArrayAdapter<SpecialEffect> {

        /* Methods */

        // Constructor
        EffectSpinnerAdapter(ArrayList<SpecialEffect> effects) {
            super(SpecialEffectsFragment.this.getContext(), R.layout.special_effects_spinner_option, effects);
        }

        // Get view
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = SpecialEffectsFragment.this.getActivity().getLayoutInflater();
            View optionView = inflater.inflate(R.layout.special_effects_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.specialEffectEffectSpinnerOptionTextView);
            SpecialEffect effect = getItem(position);

            if(effect != null) optionTextView.setText(effect.effect);
            return optionView;
        }

        // Get dropdown view
        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) { return getView(position, view, parent); }
    }

    // Special effect algorithm spinner adapter
    private class AlgorithmSpinnerAdapter extends ArrayAdapter<SpecialEffectAlgorithm> {

        /* Methods */

        // Constructor
        AlgorithmSpinnerAdapter(ArrayList<SpecialEffectAlgorithm> algorithms) {
            super(SpecialEffectsFragment.this.getContext(), R.layout.special_effects_secondary_spinner_option, algorithms);
        }

        // Get view
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = SpecialEffectsFragment.this.getActivity().getLayoutInflater();
            View optionView = inflater.inflate(R.layout.special_effects_secondary_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.specialEffectAlgorithmSpinnerOptionTextView);
            SpecialEffectAlgorithm algorithm = getItem(position);

            if(algorithm != null) optionTextView.setText(algorithm.algorithm);
            return optionView;
        }

        // Get dropdown view
        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) { return getView(position, view, parent); }
    }

    /* Methods */

    // Constructor
    public SpecialEffectsFragment() {}

    // Factory method to create new instance of fragment
    public static SpecialEffectsFragment newInstance() { return new SpecialEffectsFragment(); }

    // Check if fragment is attached to MainActivity or not
    private boolean isAttachedToMainActivity() { return activity != null; }

    // Reset kernel value
    private void resetKernel() {
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) kernel[row][col].setText(R.string.edit_text_1_text);
        }
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

                UI.disable(effectSpinnerContainer);
                UI.disable(algorithmSpinnerContainer);
                UI.disable(applyEffectButton);

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

                UI.enable(effectSpinnerContainer);
                UI.enable(algorithmSpinnerContainer);
                UI.enable(applyEffectButton);

                if(!UI.isVisible(transformedImageView)) UI.show(transformedImageView);
                if(!UI.isVisible(container)) UI.show(container);
            }
        }
    }

    // Image convolution async task
    private class ConvolutionAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if(isAttachedToMainActivity()) {
                String algorithm = ((SpecialEffectAlgorithm) algorithmSpinner.getSelectedItem()).algorithm;
                publishProgress(countProgress(1, 2));

                image.convoluteBitmap(algorithm);
                publishProgress(countProgress(2,2));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            if(isAttachedToMainActivity()) {
                UI.setUnclickable(imageView);
                UI.disable(resetButton);
                UI.disable(applyButton);

                UI.disable(effectSpinnerContainer);
                UI.disable(algorithmSpinnerContainer);
                UI.disable(applyEffectButton);

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

                UI.setInvisible(progressBar);
                UI.setClickable(imageView);
                UI.enable(resetButton);
                UI.enable(applyButton);

                UI.enable(effectSpinnerContainer);
                UI.enable(algorithmSpinnerContainer);
                UI.enable(applyEffectButton);
            }
        }
    }

}