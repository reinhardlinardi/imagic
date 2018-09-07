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
    public ArrayList<DataPoint> dataPoints;

    private double[] pmf;
    private double[] cdf;
    private int sampleCount = 0;
    private int[] newColorValueMap;
    private int[] dataCountNewValue;

    // Constructors
    Histogram() {
        viewID = 0;
        newColorValueMap = new int[256];
        dataCountNewValue = new int[256];

        pmf = new double[256];
        cdf = new double[256];

        Arrays.fill(newColorValueMap, 0);
        Arrays.fill(dataCountNewValue, 0);
        Arrays.fill(pmf,0.0);
        Arrays.fill(cdf,0.0);
    }

    Histogram(Activity activity, int viewID) {
        this.viewID = viewID;
        view = activity.findViewById(viewID);
        view.getViewport().setMinX(0f);
        view.getViewport().setMaxX((double) Image.NUM_COLOR_VALUES);
        view.getViewport().setXAxisBoundsManual(true);

        series = new BarGraphSeries<>();
        dataPoints = new ArrayList<>();

        newColorValueMap = new int[256];
        dataCountNewValue = new int[this.dataPoints.size()];

        pmf = new double[this.dataPoints.size()];
        cdf = new double[this.dataPoints.size()];

        Arrays.fill(newColorValueMap, 0);
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

        if(view != null){
            view.getViewport().setMinX(0f);
            view.getViewport().setMaxX((double) Image.NUM_COLOR_VALUES);
            view.getViewport().setXAxisBoundsManual(true);
        }

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
    protected final void render() {
        view.removeAllSeries();
        view.addSeries(series);
    }

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
    protected void cummulativeEqualizeHistogram(ArrayList<DataPoint> originalDataPoints) {
        prepareForTransformation(originalDataPoints);
        generatePMF(originalDataPoints);
        generateCDF(originalDataPoints);
        for(int it = 0; it < originalDataPoints.size(); it++) {
            newColorValueMap[it] = (int) Math.floor(cdf[it] * (double) (originalDataPoints.size() - 1));
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataCountNewValue[newColorValueMap[it]] += originalDataPoints.get(it).getY();
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataPoints.add(new DataPoint(it,dataCountNewValue[it]));
        }

        setSeriesDataPoints();
    }

    private void generatePMF(ArrayList<DataPoint> dataPoints) {
        for(int it = 0; it < dataPoints.size(); it++) {
            pmf[it] = dataPoints.get(it).getY() / (double) sampleCount;
        }
    }

    private void generateCDF(ArrayList<DataPoint> dataPoints) {
        for(int it = 0; it < dataPoints.size(); it++) {
            cdf[it] = (it == 0)? pmf[it] : pmf[it] + cdf[it-1];
        }
    }

    public int[] getNewColorValueMap() {
        return newColorValueMap;
    }

    public void linearHistogram(ArrayList<DataPoint> originalDataPoints){
        prepareForTransformation(originalDataPoints);

        int min = searchMinimumColorValue(originalDataPoints);
        int max = searchMaximumColorValue(originalDataPoints);

        for(int it = min;it <= max;it++){
            newColorValueMap[it] = 255 * (it - min)/(max - min);
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataCountNewValue[newColorValueMap[it]] += originalDataPoints.get(it).getY();
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataPoints.add(new DataPoint(it,dataCountNewValue[it]));
        }

        setSeriesDataPoints();
    }

    public void logarithmicHistogram(ArrayList<DataPoint> originalDataPoints){
        prepareForTransformation(originalDataPoints);

        int max = searchMaximumColorValue(originalDataPoints);
        double c = 1 / Math.log10((double)(1 + max));

        for(int it = 0;it <= max;it++){
            newColorValueMap[it] = (int) Math.floor(Math.log10((double)(it + 1)) * 255.0 * c);
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataCountNewValue[newColorValueMap[it]] += originalDataPoints.get(it).getY();
        }

        for(int it = 0; it < originalDataPoints.size(); it++) {
            dataPoints.add(new DataPoint(it,dataCountNewValue[it]));
        }

        setSeriesDataPoints();
    }

    private void prepareForTransformation(ArrayList<DataPoint> originalDataPoints) {
        sampleCount = 0;
        for(int it = 0; it < originalDataPoints.size(); it++) {
            sampleCount += originalDataPoints.get(it).getY();
        }
        dataPoints = new ArrayList<>();
        for(int it = 0;it<originalDataPoints.size();it++){
            newColorValueMap[it] = 0;
            dataCountNewValue[it] = 0;
        }
    }

    private int searchMaximumColorValue(ArrayList<DataPoint> originalDataPoints) {
        int max = 255;
        for(int it = originalDataPoints.size()-1;it>=0;it--){
            if(originalDataPoints.get(it).getY()>0){
                max = it;
                break;
            }
        }
        return max;
    }

    private int searchMinimumColorValue(ArrayList<DataPoint> originalDataPoints) {
        int min = 0;
        for(int it = 0;it<originalDataPoints.size();it++){
            if(originalDataPoints.get(it).getY()>0){
                min = it;
                break;
            }
        }
        return min;
    }
}

