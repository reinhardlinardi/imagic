package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HistogramActivity extends AppCompatActivity {

    // Background task
    @SuppressLint("StaticFieldLeak")
    private class HistogramTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... colors) {
            int numColors = colors.length + 1;
            int numCount = 1;
            publishProgress(countProgress(numCount, numColors));

            for(int color : colors) {
                try {
                    int[] count = Image.getColorCount(HistogramActivity.bitmap, color);

                    numCount++;
                    publishProgress(countProgress(numCount, numColors));

                    if(isCancelled()) break;
                }
                catch(Exception e) {
                    Log.e("Imagic", "Exception", e);
                }
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            HistogramActivity.this.progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            HistogramActivity.this.progressBar.setVisibility(View.GONE);
        }

        // Count progress
        private int countProgress(int taskDone, int numTask) {
            float taskDoneFraction = (float)taskDone / numTask;
            return (int)(taskDoneFraction * 100);
        }
    }

    // Image bitmap
    private static Bitmap bitmap;

    // Progress bar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            Uri imageURI = Uri.parse(bundle.getString("image"));
            progressBar = findViewById(R.id.histogramProgressBar);
            progressBar.setVisibility(View.VISIBLE);

            try {
                HistogramActivity.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                HistogramTask histogramTask = new HistogramActivity.HistogramTask();
                histogramTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Image.COLOR_RED, Image.COLOR_GREEN, Image.COLOR_BLUE, Image.COLOR_GRAYSCALE);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }

    /*
    <com.jjoe64.graphview.helper.GraphViewXML
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:title=""
        app:seriesColor="#00cc00"
        app:seriesData="0=5;2=5;3=0;4=2"
        app:seriesTitle="Foobar"
        app:seriesType="line" />
     */
}
