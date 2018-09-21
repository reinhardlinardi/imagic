package com.imagic.imagic;

import android.content.Context;

import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

class Histogram implements JSONSerializable {

    // Properties
    protected BarGraphSeries<DataPoint> series;
    public ArrayList<DataPoint> dataPoints;

    // Constructors
    Histogram() { resetData(); }

    Histogram(Histogram histogram) {
        resetData();
        dataPoints.addAll(histogram.dataPoints);
        updateSeries();
    }

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

    // Get lowest existing color value
    private int getMinColorValue() {
        int min = 0;

        for(int idx = 0; idx < 256; idx++) {
            if(dataPoints.get(idx).getY() > 0) {
                min = (int) dataPoints.get(idx).getX();
                break;
            }
        }

        return min;
    }

    // Get highest existing color value
    private int getMaxColorValue() {
        int max = 255;

        for(int idx = 255; idx >= 0; idx--) {
            if(dataPoints.get(idx).getY() > 0) {
                max = (int) dataPoints.get(idx).getX();
                break;
            }
        }

        return max;
    }

    // Get probability mass function
    private double[] getPMF() {
        int totalCount = 0;

        double[] PMF = new double[256];
        Arrays.fill(PMF, 0.0);

        for(int idx = 0; idx < 256; idx++) totalCount += dataPoints.get(idx).getY();
        for(int idx = 0; idx < 256; idx++) PMF[idx] = dataPoints.get(idx).getY() / (double) totalCount;

        return PMF;
    }

    // Get cumulative distribution function
    public double[] getCDF() {
        double[] PMF = getPMF();
        double[] CDF = new double[256];
        Arrays.fill(CDF, 0.0);

        for(int idx = 0; idx < 256; idx++) CDF[idx] = (idx == 0)? PMF[idx] : PMF[idx] + CDF[idx -1];
        return CDF;
    }

    // Linear stretching equalization
    protected int[] stretch(final double multiplier) {
        int[] newColorValue = new int[256];
        int[] newValueCount = new int[256];
        Arrays.fill(newColorValue, 0);
        Arrays.fill(newValueCount, 0);

        int min = getMinColorValue();
        int max = getMaxColorValue();

        for(int val = min; val <= max; val++) newColorValue[val] = (int)((255 * (val - min) / (max - min)) * multiplier);
        for(int idx = 0; idx < 256; idx++) newValueCount[newColorValue[idx]] += dataPoints.get(idx).getY();

        resetData();
        for(int idx = 0; idx < 256; idx++) addDataPoint(idx, newValueCount[idx]);
        updateSeries();

        return newColorValue;
    }

    // Cumulative frequency equalization
    protected int[] cumulativeFrequencyEqualization(final double multiplier) {
        int[] newColorValue = new int[256];
        int[] cumulativeValueCount = new int[256];
        Arrays.fill(newColorValue, 0);
        Arrays.fill(cumulativeValueCount, 0);

        double[] CDF = getCDF();

        for(int val = 0; val < 256; val++) newColorValue[val] = (int)((Math.floor(CDF[val] * (double) 255)) * multiplier);
        for(int idx = 0; idx < 256; idx++) cumulativeValueCount[newColorValue[idx]] += dataPoints.get(idx).getY();

        resetData();
        for(int idx = 0; idx < 256; idx++) addDataPoint(idx, cumulativeValueCount[idx]);
        updateSeries();

        return newColorValue;
    }

    // Logarithmic equalization
    protected int[] logarithmicEqualization(final double multiplier) {
        int[] newColorValue = new int[256];
        int[] newValueCount = new int[256];
        Arrays.fill(newColorValue, 0);
        Arrays.fill(newValueCount, 0);

        int max = getMaxColorValue();
        double c = 1 / Math.log10((double)(1 + max));

        for(int val = 0; val <= max; val++) newColorValue[val] = (int)(Math.floor(Math.log10((double)(val + 1)) * 255.0 * c) * multiplier);
        for(int idx = 0; idx < 256; idx++) newValueCount[newColorValue[idx]] += dataPoints.get(idx).getY();

        resetData();
        for(int idx = 0; idx < 256; idx++) addDataPoint(idx, newValueCount[idx]);
        updateSeries();

        return newColorValue;
    }

    protected int[] chromaticEqualization(){
        int[] newColorValue = new int[256];
        Arrays.fill(newColorValue,0);

        for (int idx = 0;idx<256;idx++){
            if (dataPoints.get(idx).getX() < 128){
                newColorValue[0] += dataPoints.get(idx).getY();
            } else {
                newColorValue[255] += dataPoints.get(idx).getY();
            }
        }
        return newColorValue;
    }

    public int[] matchHistogram(double[] cdfUserDefinedHistogram) {
        double[] cdfOriginalImage = getCDF();
        int[] newColorValue = new int[256];
        int[] newValueCount = new int[256];
        Arrays.fill(newColorValue, 0);
        Arrays.fill(newValueCount, 0);

        for(int i = 0; i < 256; i++) {
            for(int j = 0; j < 256; j++) {
                if(cdfOriginalImage[i] > cdfUserDefinedHistogram[j]) {
                    double d1 = (j > 0) ? cdfOriginalImage[i] - cdfUserDefinedHistogram[j-1] : 99999.0;
                    double d2 = cdfUserDefinedHistogram[j] - cdfOriginalImage[i];
                    newColorValue[i] = (d1 > d2) ? j : j-1;
                } else if(cdfOriginalImage[i] == cdfUserDefinedHistogram[j]){
                    newColorValue[i] = j;
                }
            }
        }

        for(int idx = 0; idx < 256; idx++) newValueCount[newColorValue[idx]] += dataPoints.get(idx).getY();

        resetData();
        for(int idx = 0; idx < 256; idx++) addDataPoint(idx, newValueCount[idx]);
        updateSeries();

        return newColorValue;
    }
}

