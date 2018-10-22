package com.imagic.imagic;

import android.graphics.Color;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * A class representing grayscale histogram.
 */
class GrayscaleHistogram extends ColorHistogram {

    /* Methods */

    // Constructor
    GrayscaleHistogram() {}

    // Get BarGraphSeries from data with value dependent color
    @Override
    protected BarGraphSeries<DataPoint> getBarGraphSeries() {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDataArray());

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                final float baseValue = 0.25f;
                final float extraValue = ((float)((int)(data.getX()) + 1) / NUM_OF_VALUE) / 2;
                final float totalValue = baseValue + extraValue;

                return Color.HSVToColor(new float[]{0.0f, 0.0f, totalValue});
            }
        });

        return series;
    }
}
