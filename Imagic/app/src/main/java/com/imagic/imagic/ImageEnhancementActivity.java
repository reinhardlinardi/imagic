/*
package com.imagic.imagic;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class ImageEnhancementActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private class ImageEnhancementTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... colors) {
            int numColors = colors.length;
            int numCount = 1;
            publishProgress(countProgress(numCount, numColors));

            for(int color : colors) {
                try {
                    int[] colorCount = Image.getColorCount(ImageEnhancementActivity.bitmap, color);

                    Histogram histogram = new Histogram(colorCount);
                    int[] newColorCount = histogram.equalizeHistogram();

                    switch(color) {
                        case Image.COLOR_RED:
                            newRedEqualizedValue = histogram.getNewEqualizedValue();
                            break;
                        case Image.COLOR_GREEN:
                            newGreenEqualizedValue = histogram.getNewEqualizedValue();
                            break;
                        case Image.COLOR_BLUE:
                            newBlueEqualizedValue = histogram.getNewEqualizedValue();
                            break;
                        default:
                            break;
                    }
                    DataPoint[] dataPoint = new DataPoint[Image.MAX_POSSIBLE_COLORS];
                    for(int idx = 0; idx < colorCount.length; idx++) dataPoint[idx] = new DataPoint(idx, newColorCount[idx]);

                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoint);

                    int graphViewID = getGraphViewID(color);
                    GraphView graphView = findViewById(graphViewID);
                    graphView.addSeries(series);

                    graphView.getViewport().setMinX(0);
                    graphView.getViewport().setMaxX(Image.MAX_POSSIBLE_COLORS - 1);
                    graphView.getViewport().setXAxisBoundsManual(true);

                    series.setValueDependentColor(getValueDependentColor(color));

                    numCount++;
                    publishProgress(countProgress(numCount, numColors));

                    if (isCancelled()) break;

                } catch (Exception e) {
                    Log.e("Imagic", "Exception", e);
                }
            }

            int width = ImageEnhancementActivity.bitmap.getWidth();
            int height = ImageEnhancementActivity.bitmap.getHeight();
            ImageEnhancementActivity.newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = ImageEnhancementActivity.bitmap.getPixel(col, row);
                    int alpha = Color.alpha(pixel);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    ImageEnhancementActivity.newBitmap.setPixel(col, row, Color.argb(alpha,
                            newRedEqualizedValue[red],
                            newGreenEqualizedValue[green],
                            newBlueEqualizedValue[blue]));
                }
            }

            return null;
        }

//        @Override
        protected void onProgressUpdate(Integer... progress) {
            ImageEnhancementActivity.this.progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void results) {
            findViewById(R.id.redGraphView).setVisibility(View.VISIBLE);
            findViewById(R.id.greenGraphView).setVisibility(View.VISIBLE);
            findViewById(R.id.blueGraphView).setVisibility(View.VISIBLE);

            ImageEnhancementActivity.this.progressBar.setVisibility(View.GONE);
            Toast.makeText(ImageEnhancementActivity.this, "Histogram generated", Toast.LENGTH_SHORT).show();
            new ImageLoadingTask(ImageEnhancementActivity.this.imageViewBefore).execute(ImageEnhancementActivity.bitmap);
            new ImageLoadingTask(ImageEnhancementActivity.this.imageViewResult).execute(ImageEnhancementActivity.newBitmap);
        }

        // Count progress
        private int countProgress(int taskDone, int numTask) {
            float taskDoneFraction = (float)taskDone / numTask;
            return (int)(taskDoneFraction * 100);
        }

        // Get graph view ID
        private int getGraphViewID(int color) {
            int graphViewID = 0;

            switch(color) {
                case Image.COLOR_RED: graphViewID = R.id.redGraphView; break;
                case Image.COLOR_GREEN: graphViewID = R.id.greenGraphView; break;
                case Image.COLOR_BLUE: graphViewID = R.id.blueGraphView; break;
                case Image.COLOR_GRAYSCALE: graphViewID = R.id.grayscaleGraphView; break;
                default: break;
            }

            return graphViewID;
        }

        // Get data color
        private int getDataColor(DataPoint data, int color) {
            int dataColor = 0;

            if(color == Image.COLOR_GRAYSCALE) {
                float baseValue = 0.75f;
                float extraValue = ((float)((int)(data.getX()) + 1) / Image.MAX_POSSIBLE_COLORS) / 2;
                float totalValue = baseValue - extraValue;

                dataColor = Color.HSVToColor(new float[]{0.0f, 0.0f, totalValue});
            }
            else {
                float baseSaturation = 0.25f;
                float extraSaturation = ((float)((int)(data.getX()) + 1) / Image.MAX_POSSIBLE_COLORS) / 2;
                float totalSaturation = baseSaturation + extraSaturation;

                switch(color) {
                    case Image.COLOR_RED: dataColor = Color.HSVToColor(new float[]{0.0f, totalSaturation, 0.9f}); break;
                    case Image.COLOR_GREEN: dataColor = Color.HSVToColor(new float[]{120.0f, totalSaturation, 0.9f}); break;
                    case Image.COLOR_BLUE: dataColor = Color.HSVToColor(new float[]{240.0f, totalSaturation, 0.9f}); break;
                    default: break;
                }
            }

            return dataColor;
        }

        // Get value dependent color
        private ValueDependentColor<DataPoint> getValueDependentColor(final int color) {
            return new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return getDataColor(data, color);
                }
            };
        }
    }

    // Image bitmap
    private static Bitmap bitmap;
    private static Bitmap newBitmap;
    private int[] newRedEqualizedValue;
    private int[] newGreenEqualizedValue;
    private int[] newBlueEqualizedValue;

    // Progress bar
    private ProgressBar progressBar;
    private ImageView imageViewBefore;
    private ImageView imageViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enhancement);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            Uri imageURI = Uri.parse(bundle.getString("image"));

            progressBar = findViewById(R.id.histogramProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            imageViewBefore = findViewById(R.id.iv_before);
            imageViewResult = findViewById(R.id.iv_result);

            try {
                ImageEnhancementActivity.bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                ImageEnhancementActivity.ImageEnhancementTask imageEnhancementTask = new ImageEnhancementActivity.ImageEnhancementTask();
                imageEnhancementTask.execute(Image.COLOR_RED, Image.COLOR_GREEN, Image.COLOR_BLUE);
            }
            catch(Exception e) {
                Log.e("Imagic", "Exception", e);
            }
        }
    }
}
*/