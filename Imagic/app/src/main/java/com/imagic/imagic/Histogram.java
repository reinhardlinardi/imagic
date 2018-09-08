package com.imagic.imagic;

import android.content.Context;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

abstract class Histogram implements JSONSerializable {

    // Properties
    protected BarGraphSeries<DataPoint> series;
    public ArrayList<DataPoint> dataPoints;
    /*
    private double[] pmf;
    private double[] cdf;
    private int sampleCount = 0;
    private int[] newColorValueMap;
    private int[] dataCountNewValue;
    */
    // Constructors
    Histogram() {
        resetData();
        /*
        newColorValueMap = new int[256];
        dataCountNewValue = new int[256];

        pmf = new double[256];
        cdf = new double[256];

        Arrays.fill(newColorValueMap, 0);
        Arrays.fill(dataCountNewValue, 0);
        Arrays.fill(pmf,0.0);
        Arrays.fill(cdf,0.0);
        */
    }
    /*
    Histogram(Activity activity, int viewID) {
        this.viewID = viewID;
        view = activity.findViewById(viewID);
        view.getViewport().setMinX(0f);
        view.getViewport().setMaxX(255);
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
    */

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject histogramJSON = new JSONObject();
        JSONArray dataPointsJSON = new JSONArray();

        for(DataPoint dataPoint : dataPoints) {
            JSONArray dataPointJSON = new JSONArray();
            dataPointJSON.put(dataPoint.getX());
            dataPointJSON.put(dataPoint.getY());
            dataPointsJSON.put(dataPointJSON);
        }

        histogramJSON.put("dataPoints", dataPointsJSON);
        return histogramJSON.toString();
    }

    @Override
    public void jsonDeserialize(Context context, String json) throws Exception {
        resetData();

        JSONObject histogramJSON = new JSONObject(json);
        JSONArray dataPointsJSON = histogramJSON.getJSONArray("dataPoints");

        for(int idx = 0; idx < dataPointsJSON.length(); idx++) {
            JSONArray dataPointJSON = dataPointsJSON.getJSONArray(idx);
            dataPoints.add(new DataPoint(dataPointJSON.getDouble(0), dataPointJSON.getDouble(1)));
        }

        updateSeries();
    }

    // Enable value dependent color
    protected abstract void enableValueDependentColor();

    // Is histogram data empty
    protected final boolean isDataEmpty() { return dataPoints.size() == 0; }

    // Reset data
    protected final void resetData() {
        series = new BarGraphSeries<>();
        dataPoints = new ArrayList<>();
    }

    // Add data point
    protected final void addDataPoint(double x, double y) { dataPoints.add(new DataPoint(x, y)); }

    // Update series
    protected final void updateSeries() {
        DataPoint[] dataPointArray = new DataPoint[dataPoints.size()];
        for(int idx = 0; idx < dataPoints.size(); idx++) dataPointArray[idx] = dataPoints.get(idx);
        series = new BarGraphSeries<>(dataPointArray);
    }

    // --------------------------------------------------------------
    /*
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

        updateSeries();
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

        updateSeries();
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

        updateSeries();
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
    */
}

