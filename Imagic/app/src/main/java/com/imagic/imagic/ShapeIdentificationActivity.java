package com.imagic.imagic;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Arrays;

public class ShapeIdentificationActivity extends AppCompatActivity{

    // Image load async task
    private class ImageLoadTask extends AsyncTask<Uri, Integer, Void> {
        @Override
        protected Void doInBackground(Uri... URIs) {
            int numImages = URIs.length * 2;
            int done = 0;
            publishProgress(countProgress(done + 1, numImages + 1));

            for(Uri URI : URIs) {
                try {
                    Image noBitmapImage = JSONSerializer.deserialize(ShapeIdentificationActivity.this, Cache.read(URI), Image.class);

                    originalImage = new Image(ShapeIdentificationActivity.this, noBitmapImage, true);
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
            UI.updateImageView(ShapeIdentificationActivity.this, originalImage.uri, beforeView);
            UI.clearImageViewMemory(ShapeIdentificationActivity.this);
            UI.setInvisible(progressBar);

            if(dataAvailableInCache()) {
                originalImage.rgb.enableValueDependentColor();
            }
        }
    }

    // Contrast enhancement async task
    private class ChromaticTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ChainCode chainCode = new ChainCode();
            int[][] matrix = originalImage.getChromaticMatrix();
            chainCode.countDirectionCode(matrix);
            prediction = chainCode.predict();

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
            predictionResultView.setText(Integer.toString(prediction));
            UI.setInvisible(progressBar);
        }
    }

    // Option adapter
    private class ShapeIdentificationAdapter extends ArrayAdapter<ShapeIdentificationOption> {

        ShapeIdentificationAdapter(ArrayList<ShapeIdentificationOption> options) {
            super(ShapeIdentificationActivity.this, R.layout.shape_identification_spinner_option, options);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = ShapeIdentificationActivity.this.getLayoutInflater();
            View optionView = inflater.inflate(R.layout.shape_identification_spinner_option, parent, false);

            TextView optionTextView = optionView.findViewById(R.id.shapeIdentificationSpinnerOptionTextView);
            ShapeIdentificationOption option = getItem(position);

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

    // UI components
    private ProgressBar progressBar;
    private ImageView beforeView;
    private TextView predictionResultView;
    private Button predictButton;

    // Selected option
    private ShapeIdentificationOption selectedOption;

    int prediction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_identification);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) cachedImageDataURI = Uri.parse(bundle.getString(Cache.INTENT_BUNDLE_NAME));

        progressBar = findViewById(R.id.shapeIdentificationProgressBar);
        beforeView = findViewById(R.id.shapeIdentificationImageBefore);
        predictionResultView = findViewById(R.id.shapeIdentificationVerdict);
        predictButton = findViewById(R.id.shapeIdentificationButton);

        predictButton.setOnClickListener(getButtonOnClickListener());

        try {
            ArrayList<ShapeIdentificationOption> options = JSONSerializer.arrayDeserialize(this, Text.readRawResource(this, R.raw.shape_identification_options), ShapeIdentificationOption.class);
            ShapeIdentificationActivity.ShapeIdentificationAdapter adapter = new ShapeIdentificationActivity.ShapeIdentificationAdapter(options);

            Spinner spinner = findViewById(R.id.shapeIdentificationAlgorithmSpinner);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(getSpinnerOnItemSelectedListener());
        }
        catch(Exception e) {
            Log.e("Imagic", "Exception", e);
        }

        ShapeIdentificationActivity.ImageLoadTask imageLoadTask = new ShapeIdentificationActivity.ImageLoadTask();
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
                selectedOption = (ShapeIdentificationOption) adapterView.getItemAtPosition(position);
                String selectedAlgorithm = selectedOption.algorithm;

                TextView textView = (TextView) view;
                textView.setText(selectedAlgorithm + "   ▾");
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
                //Panggil predicition berdasarkan algo
                ShapeIdentificationActivity.ChromaticTask chromaticTask = new ShapeIdentificationActivity.ChromaticTask();
                chromaticTask.execute();
            }
        };
    }

    // Check if data is available in cache
    private boolean dataAvailableInCache() { return !(originalImage.rgb.isDataEmpty()); }
}
