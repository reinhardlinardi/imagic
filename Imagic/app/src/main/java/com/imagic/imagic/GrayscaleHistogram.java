package com.imagic.imagic;

import android.app.Activity;
import android.graphics.Color;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.DataPoint;

class GrayscaleHistogram extends Histogram {

    // Constructors
    GrayscaleHistogram() { super(); }
    GrayscaleHistogram(Activity activity, int viewID) { super(activity, viewID); }

    @Override
    protected void enableValueDependentColor() {
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                float baseValue = 0.75f;
                float extraValue = ((float)((int)(data.getX()) + 1) / 256) / 2;
                float totalValue = baseValue - extraValue;

                return Color.HSVToColor(new float[]{0.0f, 0.0f, totalValue});
            }
        });
    }
}
