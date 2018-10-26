package com.imagic.imagic;

import java.util.Arrays;

/**
 * A class representing histogram for color type with 256 values (black and white excluded).
 * X represents color value (0-255/0x0-0xFF) and Y represents frequency of the color value.
 */
class ColorHistogram extends Histogram {

    /* Constants */
    static final int NUM_OF_VALUE =  256;
    static final int MIN_VALUE = 0;
    static final int MAX_VALUE = 255;

    /* Methods */

    // Constructor
    ColorHistogram() {}

    ColorHistogram(ColorHistogram colorHistogram) { super(colorHistogram); }

    // Get color value from DataPoint
    protected int getColorValue(int idx) { return (int)(data.get(idx).getX()); }

    // Get frequency from DataPoint
    protected int getFrequency(int idx) { return (int)(data.get(idx).getY()); }

    // Set data from data array
    protected final void setData(int[] dataArray) {
        resetData();
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) addData(idx, dataArray[idx]);
    }

    // Get minimum color value with non-zero frequency
    private int getMinColorValue() {
        int min = MIN_VALUE;

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) {
            if(getFrequency(idx) > 0) {
                min = getColorValue(idx);
                break;
            }
        }

        return min;
    }

    // Get maximum color value with non-zero frequency
    private int getMaxColorValue() {
        int max = MAX_VALUE;

        for(int idx = MAX_VALUE; idx >= MIN_VALUE; idx--) {
            if(getFrequency(idx) > 0) {
                max = getColorValue(idx);
                break;
            }
        }

        return max;
    }

    // Get PMF (probability mass function) of histogram
    protected double[] getPMF() {
        int totalFrequency = 0;

        double[] PMF = new double[NUM_OF_VALUE];
        Arrays.fill(PMF, 0.0);

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) totalFrequency += getFrequency(idx);
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) PMF[idx] = (double) getFrequency(idx) / totalFrequency;

        return PMF;
    }

    // Get CDF (cumulative distribution function) of histogram
    protected double[] getCDF() {
        double[] PMF = getPMF();
        double[] CDF = new double[NUM_OF_VALUE];
        Arrays.fill(CDF, 0.0);

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) CDF[idx] = (idx == MIN_VALUE)? PMF[idx] : PMF[idx] + CDF[idx - 1];
        return CDF;
    }

    // Linear stretching, return mapping
    protected int[] linearStretch(double multiplier) {
        int[] mapping = new int[NUM_OF_VALUE];
        int[] frequency = new int[NUM_OF_VALUE];

        Arrays.fill(mapping, 0);
        Arrays.fill(frequency, 0);

        int min = getMinColorValue();
        int max = getMaxColorValue();

        for(int val = min; val <= max; val++) mapping[val] = (int)((MAX_VALUE * (val - min) / (max - min)) * multiplier);
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) frequency[mapping[idx]] += getFrequency(idx);

        resetData();
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) addData(idx, frequency[idx]);

        return mapping;
    }

    // CDF stretching, return mapping
    protected int[] cdfStretch(double multiplier) {
        int[] mapping = new int[NUM_OF_VALUE];
        int[] frequency = new int[NUM_OF_VALUE];

        Arrays.fill(mapping, 0);
        Arrays.fill(frequency, 0);

        double[] CDF = getCDF();
        for(int val = MIN_VALUE; val <= MAX_VALUE; val++) mapping[val] = (int)((Math.floor(CDF[val] * (double)MAX_VALUE)) * multiplier);
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) frequency[mapping[idx]] += getFrequency(idx);

        resetData();
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) addData(idx, frequency[idx]);

        return mapping;
    }

    // Logarithmic stretching, return mapping
    protected int[] logarithmicStretch(double multiplier) {
        int[] mapping = new int[NUM_OF_VALUE];
        int[] frequency = new int[NUM_OF_VALUE];

        Arrays.fill(mapping, 0);
        Arrays.fill(frequency, 0);

        int max = getMaxColorValue();
        double c = 1 / Math.log10((double)(1 + max));

        for(int val = MIN_VALUE; val <= max; val++) mapping[val] = (int)(Math.floor(Math.log10((double)(val + 1)) * (double)MAX_VALUE * c) * multiplier);
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) frequency[mapping[idx]] += getFrequency(idx);

        resetData();
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) addData(idx, frequency[idx]);

        return mapping;
    }

    // Histogram matching, return mapping
    protected int[] match(double[] userCDF) {
        double[] cdf = getCDF();
        double INF = 100000.0;

        int[] mapping = new int[NUM_OF_VALUE];
        int[] frequency = new int[NUM_OF_VALUE];

        Arrays.fill(mapping, 0);
        Arrays.fill(frequency, 0);

        for(int cdfIdx = MIN_VALUE; cdfIdx <= MAX_VALUE; cdfIdx++) {
            for(int userCDFIdx = MIN_VALUE; userCDFIdx <= MAX_VALUE; userCDFIdx++) {
                if(cdf[cdfIdx] > userCDF[userCDFIdx]) {
                    double d1 = (userCDFIdx > 0)? cdf[cdfIdx] - userCDF[userCDFIdx - 1] : INF;
                    double d2 = userCDF[userCDFIdx] - cdf[cdfIdx];

                    mapping[cdfIdx] = (d1 > d2)? userCDFIdx : userCDFIdx - 1;
                }
                else if(cdf[cdfIdx] == userCDF[userCDFIdx]) mapping[cdfIdx] = userCDFIdx;
            }
        }

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) frequency[mapping[idx]] += getFrequency(idx);

        resetData();
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) addData(idx, frequency[idx]);

        return mapping;
    }
}
