package com.imagic.imagic;

import android.graphics.Color;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * A class representing red histogram.
 */
class RedHistogram extends ColorHistogram {

    /* Methods */

    // Constructor
    RedHistogram() {}

    // Get BarGraphSeries from data with value dependent color
    @Override
    protected BarGraphSeries<DataPoint> getBarGraphSeries() {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDataArray());

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                final float baseSaturation = 0.25f;
                final float extraSaturation = ((float)((int)(data.getX()) + 1) / NUM_OF_VALUE) / 2;
                final float totalSaturation = baseSaturation + extraSaturation;

                return Color.HSVToColor(new float[]{0.0f, totalSaturation, 0.9f});
            }
        });

        return series;
    }
}
