package com.imagic.imagic;

import android.util.Log;

import java.util.Arrays;

public class Histogram {
    private int[] dataCount;
    private int sampleCount = 0;
    private double[] pmf;
    private double[] cdf;
    private int[] newEqualizedValue;
    private int[] dataCountNewValue;

    public Histogram(int[] dataCount) {
        this.dataCount = dataCount.clone();
        for(int it = 0; it < this.dataCount.length; it++) {
            sampleCount += this.dataCount[it];
        }
        newEqualizedValue = new int[this.dataCount.length];
        dataCountNewValue = new int[this.dataCount.length];
        pmf = new double[this.dataCount.length];
        cdf = new double[this.dataCount.length];

        Arrays.fill(newEqualizedValue, 0);
        Arrays.fill(dataCountNewValue, 0);
        Arrays.fill(pmf,0.0);
        Arrays.fill(cdf,0.0);
    }

    public int[] equalizeHistogram() {
        generatePMF();
        generateCDF();
        for(int it = 0; it < this.dataCount.length; it++) {
            newEqualizedValue[it] = (int) (cdf[it] * (double) (this.dataCount.length - 1));
        }

        for(int it = 0; it < this.dataCount.length; it++) {
            dataCountNewValue[newEqualizedValue[it]] += dataCount[it];
        }

        return dataCountNewValue;
    }

    public void generatePMF() {
        for(int it = 0; it < this.dataCount.length; it++) {
            pmf[it] = (double) this.dataCount[it] / (double) sampleCount;
        }
    }

    public void generateCDF() {
        for(int it = 0; it < this.dataCount.length; it++) {
            cdf[it] = (it == 0)? pmf[it] : pmf[it] + cdf[it-1];
        }
    }

    public int[] getNewEqualizedValue() {
        return newEqualizedValue;
    }
}
