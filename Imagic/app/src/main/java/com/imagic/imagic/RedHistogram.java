package com.imagic.imagic;

import android.app.Activity;
import android.graphics.Color;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.DataPoint;

class RedHistogram extends Histogram {

    // Constructor
    RedHistogram(Activity activity, int viewID) { super(activity, viewID); }

    @Override
    protected void enableValueDependentColor() {
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                float baseSaturation = 0.25f;
                float extraSaturation = ((float)((int)(data.getX()) + 1) / Image.NUM_COLOR_VALUES) / 2;
                float totalSaturation = baseSaturation + extraSaturation;

                return Color.HSVToColor(new float[]{0.0f, totalSaturation, 0.9f});
            }
        });
    }
}
