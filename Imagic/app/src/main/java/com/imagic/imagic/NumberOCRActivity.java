package com.imagic.imagic;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class NumberOCRActivity extends AppCompatActivity {

    // Image load async task
    private class ImageLoadTask extends AsyncTask<Uri, Integer, Void> {
        @Override
        protected Void doInBackground(Uri... URIs) {
            int numImages = URIs.length * 2;
            int done = 0;
            publishProgress(countProgress(done + 1, numImages + 1));

            for(Uri URI : URIs) {
                try {
                    Image noBitmapImage = JSONSerializer.deserialize(NumberOCRActivity.this, Cache.read(URI), Image.class);

                    originalImage = new Image(NumberOCRActivity.this, noBitmapImage, true);
                    publishProgress(countProgress((++done) + 1, numImages + 1));

                    skeletonImage = new Image(NumberOCRActivity.this, originalImage, false);
                    publishProgress(countProgress((++done) + 1, numImages + 1));

                    if(isCancelled()) break;
                }
                catch(Exception e) {
                    Log.e("Imagic", "Exception", e);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Void results) {
            UI.updateImageView(NumberOCRActivity.this, originalImage.uri, originalImageView);
            UI.updateImageView(NumberOCRActivity.this, skeletonImage.uri, skeletonImageView);
            UI.clearImageViewMemory(NumberOCRActivity.this);
            UI.setInvisible(progressBar);
        }
    }

    // OCR async task
    private class OCRTask extends AsyncTask<Void, Integer, Character> {
        @Override
        protected Character doInBackground(Void... voids) {
            publishProgress(countProgress(1,3));
            Character verdict = ' ';
            int verdictInt;
            try {
                skeletonImage = new Image(NumberOCRActivity.this, originalImage, false);
                skeletonImage.getBlackWhiteMatrix(128);
                NumberOCROption selectedOption = (NumberOCROption) algorithmSpinner.getSelectedItem();

                switch(selectedOption.algorithm) {
                    case "Edge Detection":
                        ChainCode chainCode = new ChainCode();
                        chainCode.getEdgeDetectionChainCode(skeletonImage.blackWhiteMatrix);
                        publishProgress(countProgress(2,3));

                        verdictInt = chainCode.edgeDetectionOCR();
                        verdict = (char)('0' + verdictInt);
                        publishProgress(countProgress(3,3));
                        break;
                    case "Thinning":
                        skeletonImage.getSkeleton();
                        publishProgress(countProgress(2,3));

                        try {
                            skeletonImage.setBitmapToSkeleton(NumberOCRActivity.this);
                        }
                        catch(Exception e) {
                            Log.e("Imagic", "Exception", e);
                        }

                        verdict = skeletonImage.skeleton.getPrediction();
                        publishProgress(countProgress(3,3));
                        break;
                    case "Thinning (Number)":
                        skeletonImage.getSkeleton();
                        publishProgress(countProgress(2,3));

                        try {
                            skeletonImage.setBitmapToSkeleton(NumberOCRActivity.this);
                        }
                        catch(Exception e) {
                            Log.e("Imagic", "Exception", e);
                        }

                        verdictInt = skeletonImage.skeleton.getNumberPrediction();
                        verdict = (char) ('0' + verdictInt);
                        publishProgress(countProgress(3,3));
                        break;
                }
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }

            return verdict;
        }

        @Override
        protected void onPreExecute() {
            UI.disable(predictButton);
            UI.disable(algorithmSpinner);

            progressBar.setProgress(0);
            UI.show(progressBar);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { progressBar.setProgress(progress[0]); }

        @Override
        protected void onPostExecute(Character results) {
            NumberOCROption selectedOption = (NumberOCROption) algorithmSpinner.getSelectedItem();

            switch(selectedOption.algorithm) {
                case "Edge Detection":
                    verdictTextView.setText(Character.toString(results));
                    break;
                case "Thinning (Number)":
                case "Thinning":
                    UI.updateImageView(NumberOCRActivity.this, skeletonImage.bitmap, skeletonImageView);
                    UI.clearImageViewMemory(NumberOCRActivity.this);
                    verdictTextView.setText(Character.toString(results));
                    break;
            }

            UI.setInvisible(progressBar);
            UI.enable(predictButton);
            UI.enable(algorithmSpinner);
        }
    }

    // Option adapter
    private class NumberOCRAdapter extends ArrayAdapter<NumberOCROption> {

        NumberOCRAdapter(ArrayList<NumberOCROption> options) {
            super(NumberOCRActivity.this, R.layout.number_ocr_spinner_option, options);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = NumberOCRActivity.this.getLayoutInflater();
            View optionView = inflater.inflate(R.layout.number_ocr_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.numberOCRSpinnerOptionTextView);
            NumberOCROption option = getItem(position);

            if(option != null) optionTextView.setText(option.algorithm);

            return optionView;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            return getView(position, view, parent);
        }
    }

    // Cached image data URI
    private Uri cachedImageDataURI;

    // Image
    private Image originalImage;
    private Image skeletonImage;

    // UI components
    private ProgressBar progressBar;
    private ImageView originalImageView;
    private ImageView skeletonImageView;
    private Spinner algorithmSpinner;
    private Button predictButton;
    private TextView verdictLabelTextView;
    private TextView verdictTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_ocr);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.numberOCRProgressBar);
        originalImageView = findViewById(R.id.numberOCRImage);
        skeletonImageView = findViewById(R.id.numberOCRSkeleton);

        predictButton = findViewById(R.id.numberOCRButton);
        predictButton.setOnClickListener(getButtonOnClickListener());

        verdictLabelTextView = findViewById(R.id.numberOCRVerdictLabel);
        verdictTextView = findViewById(R.id.numberOCRVerdict);

        try {
            ArrayList<NumberOCROption> options = JSONSerializer.arrayDeserialize(this, Text.readRawResource(this, R.raw.number_ocr_options), NumberOCROption.class);
            NumberOCRAdapter adapter = new NumberOCRActivity.NumberOCRAdapter(options);

            algorithmSpinner = findViewById(R.id.numberOCRAlgorithmSpinner);
            algorithmSpinner.setAdapter(adapter);
            algorithmSpinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }

        ImageLoadTask imageLoadTask = new ImageLoadTask();
        imageLoadTask.execute(cachedImageDataURI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI.clearImageViewMemory(this);
    }

    // Count progress
    private int countProgress(int numTaskDone, int totalNumTask) {
        float taskDoneFraction = (float) numTaskDone / totalNumTask;
        return (int)(taskDoneFraction * 100);
    }

    // Spinner on item selected listener
    private AdapterView.OnItemSelectedListener getSpinnerOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                NumberOCROption selectedOption = (NumberOCROption) adapterView.getItemAtPosition(position);
                TextView textView = (TextView) view;
                textView.setText(selectedOption.algorithm + "   ▾");

                switch(selectedOption.algorithm) {
                    case "Edge Detection":
                        UI.hide(skeletonImageView);
                        break;
                    case "Thinning (Number)":
                    case "Thinning":
                        UI.show(skeletonImageView);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    // Button on click listener
    private View.OnClickListener getButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OCRTask OCRTask = new OCRTask();
                OCRTask.execute();
            }
        };
    }
}
