package com.imagic.imagic;

import android.graphics.Color;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.DataPoint;

class GreenHistogram extends Histogram {

    // Constructors
    GreenHistogram() {}

    GreenHistogram(GreenHistogram greenHistogram) { super(greenHistogram); }

    protected void enableValueDependentColor() {
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                float baseSaturation = 0.25f;
                float extraSaturation = ((float)((int)(data.getX()) + 1) / 256) / 2;
                float totalSaturation = baseSaturation + extraSaturation;

                return Color.HSVToColor(new float[]{120.0f, totalSaturation, 0.9f});
            }
        });
    }
}
