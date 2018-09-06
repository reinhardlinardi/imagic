package com.imagic.imagic;

import android.app.Activity;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

abstract class Histogram implements JSONSerializable {

    // Properties
    protected int viewID;
    protected GraphView view;
    protected BarGraphSeries<DataPoint> series;
    protected ArrayList<DataPoint> dataPoints;

    // Constructors
    Histogram() { viewID = 0; }

    Histogram(Activity activity, int viewID) {
        this.viewID = viewID;
        view = activity.findViewById(viewID);
        view.getViewport().setMinX(0f);
        view.getViewport().setMaxX((double) Image.NUM_COLOR_VALUES);
        view.getViewport().setXAxisBoundsManual(true);

        series = new BarGraphSeries<>();
        dataPoints = new ArrayList<>();
    }

    // Check if histogram is not initialized
    public boolean isUninitialized() { return viewID == 0; }

    @Override
    public final String jsonSerialize() throws Exception {
        JSONObject histogramJSON = new JSONObject();
        histogramJSON.put("viewID", viewID);

        JSONArray dataPointsArray = new JSONArray();

        for(DataPoint dataPoint : dataPoints) {
            JSONArray dataPointArray = new JSONArray();
            dataPointArray.put(dataPoint.getX());
            dataPointArray.put(dataPoint.getY());
            dataPointsArray.put(dataPointArray);
        }

        histogramJSON.put("dataPoints", dataPointsArray);
        return histogramJSON.toString();
    }

    @Override
    public final void jsonDeserialize(Activity activity, String json) throws Exception {
        JSONObject histogramJSON = new JSONObject(json);
        viewID = histogramJSON.getInt("viewID");
        view = activity.findViewById(viewID);

        view.getViewport().setMinX(0f);
        view.getViewport().setMaxX((double) Image.NUM_COLOR_VALUES);
        view.getViewport().setXAxisBoundsManual(true);

        series = new BarGraphSeries<>();
        dataPoints = new ArrayList<>();

        JSONArray dataPointsArray = histogramJSON.getJSONArray("dataPoints");

        for(int idx = 0; idx < dataPointsArray.length(); idx++) {
            JSONArray dataPointArray = dataPointsArray.getJSONArray(idx);
            dataPoints.add(new DataPoint(dataPointArray.getDouble(0), dataPointArray.getDouble(1)));
        }

        setSeriesDataPoints();
    }

    // Show histogram
    protected final void show() { view.setVisibility(View.VISIBLE); }

    // Hide histogram
    protected final void hide() { view.setVisibility(View.INVISIBLE); }

    // Render histogram
    protected final void render() { view.addSeries(series); }

    // Add data point
    protected final void addDataPoint(double x, double y) { dataPoints.add(new DataPoint(x, y)); }

    // Set series data points
    protected final void setSeriesDataPoints() {
        DataPoint[] dataPointArray = new DataPoint[dataPoints.size()];
        for(int idx = 0; idx < dataPoints.size(); idx++) dataPointArray[idx] = dataPoints.get(idx);
        series = new BarGraphSeries<>(dataPointArray);
    }

    // Enable value dependent color
    protected abstract void enableValueDependentColor();
}
