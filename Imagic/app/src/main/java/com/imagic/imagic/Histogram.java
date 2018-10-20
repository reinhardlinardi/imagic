package com.imagic.imagic;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

/**
 * A class representing general purpose histogram.
 */
class Histogram {

    /* Properties */

    // ArrayList of histogram's data
    ArrayList<DataPoint> data;

    /* Methods */

    // Constructor
    Histogram() { resetData(); }

    // Check if histogram contains any data
    protected final boolean isEmpty() { return data.size() == 0; }

    // Reset all data
    protected final void resetData() { data = new ArrayList<>(); }

    // Add data point
    protected final void addData(double x, double y) { data.add(new DataPoint(x, y)); }

    // Get array of DataPoint from data
    protected final DataPoint[] getDataArray() {
        DataPoint[] dataArray = new DataPoint[data.size()];
        for(int idx = 0; idx < data.size(); idx++) dataArray[idx] = data.get(idx);

        return dataArray;
    }

    // Get BarGraphSeries from data
    protected BarGraphSeries<DataPoint> getBarGraphSeries() { return new BarGraphSeries<>(getDataArray()); }
}
