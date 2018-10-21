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

    // Get color value from DataPoint
    private int getColorValue(int idx) { return (int)(data.get(idx).getX()); }

    // Get frequency from DataPoint
    private int getFrequency(int idx) { return (int)(data.get(idx).getY()); }

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
    private double[] getPMF() {
        int totalFrequency = 0;

        double[] PMF = new double[NUM_OF_VALUE];
        Arrays.fill(PMF, 0.0);

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) totalFrequency += getFrequency(idx);
        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) PMF[idx] = (double) getFrequency(idx) / totalFrequency;

        return PMF;
    }

    // Get CDF (cumulative distribution function) of histogram
    private double[] getCDF() {
        double[] PMF = getPMF();
        double[] CDF = new double[NUM_OF_VALUE];
        Arrays.fill(CDF, 0.0);

        for(int idx = MIN_VALUE; idx <= MAX_VALUE; idx++) CDF[idx] = (idx == MIN_VALUE)? PMF[idx] : PMF[idx] + CDF[idx - 1];

        return CDF;
    }


}
