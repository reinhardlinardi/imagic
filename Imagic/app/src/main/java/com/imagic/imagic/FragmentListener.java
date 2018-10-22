package com.imagic.imagic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * An interface to help communication between from fragment to MainActivity.
 *
 * MainActivity have to implement this interface as well as all of its methods.
 * Each fragment will hold a reference to MainActivity in a "communicator variable" on attach.
 * That "communicator variable" data type is this interface.
 * We can call a method in MainActivity from fragment by calling the method from our "communicator variable" in that fragment.
 */
interface FragmentListener {

    // Update URI on image intent result
    void onImageIntentResult(int requestCode, int resultCode, Intent data);

    // Check if image has no bitmap yet
    boolean isImageHasBitmap();

    // Check if RGB histogram data is available
    boolean isRGBHistogramDataAvailable();

    // Check if grayscale histogram data
    boolean isGrayscaleHistogramDataAvailable();

    // Get image URI
    Uri getImageURI();

    // Get image bitmap
    Bitmap getImageBitmap();

    // Get histogram bar graph series data
    BarGraphSeries<DataPoint> getHistogramBarGraphSeriesData(ColorType colorType);

    // Load image bitmap
    void loadImageBitmap() throws Exception;

    // Reset all histogram data
    void resetAllHistogramData();

    // Update histogram data
    void updateHistogramData(ColorType colorType);
}
