package com.imagic.imagic;

import android.app.Activity;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

abstract class Histogram implements JSONSerializable {

    // Properties
    protected int viewID;
    protected GraphView view;
    protected BarGraphSeries<DataPoint> series;
    protected ArrayList<DataPoint> dataPoints;
    protected ArrayList<DataPoint> newDataPoints;

    private double[] pmf;
    private double[] cdf;
    private int sampleCount = 0;
    private int[] newEqualizedValue;
    private int[] dataCountNewValue;

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
        newDataPoints = new ArrayList<>();

        newEqualizedValue = new int[this.dataPoints.size()];
        dataCountNewValue = new int[this.dataPoints.size()];

        pmf = new double[this.dataPoints.size()];
        cdf = new double[this.dataPoints.size()];

        Arrays.fill(newEqualizedValue, 0);
        Arrays.fill(dataCountNewValue, 0);
        Arrays.fill(pmf,0.0);
        Arrays.fill(cdf,0.0);

        for(int it = 0; it < this.dataPoints.size(); it++) {
            sampleCount += this.dataPoints.get(it).getY();
        }
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

    //Equalization Part
    protected void cummulativeEqualizeHistogram() {
        generatePMF();
        generateCDF();
        for(int it = 0; it < this.dataPoints.size(); it++) {
            newEqualizedValue[it] = (int) (cdf[it] * (double) (this.dataPoints.size() - 1));
        }

        for(int it = 0; it < this.dataPoints.size(); it++) {
            dataCountNewValue[newEqualizedValue[it]] += dataPoints.get(it).getY();
        }

        for(int it = 0; it < this.dataPoints.size(); it++) {
            newDataPoints.add(new DataPoint(it,dataCountNewValue[it]));
        }
    }

    private void generatePMF() {
        for(int it = 0; it < this.dataPoints.size(); it++) {
            pmf[it] = (double) this.dataPoints.get(it).getY() / (double) sampleCount;
        }
    }

    private void generateCDF() {
        for(int it = 0; it < this.dataPoints.size(); it++) {
            cdf[it] = (it == 0)? pmf[it] : pmf[it] + cdf[it-1];
        }
    }

    public int[] getNewEqualizedValue() {
        return newEqualizedValue;
    }
}
