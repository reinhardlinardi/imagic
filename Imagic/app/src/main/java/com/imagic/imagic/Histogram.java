package com.imagic.imagic;

import java.util.Arrays;

public class Histogram {
    private int[] dataCount;
    private int sampleCount = 0;
    private double[] pmf;
    private double[] cdf;

    public Histogram(int[] dataCount) {
        this.dataCount = dataCount.clone();
        for(int it = 0; it < this.dataCount.length; it++) {
            sampleCount += this.dataCount[it];
        }
        pmf = new double[this.dataCount.length];
        cdf = new double[this.dataCount.length];
        Arrays.fill(pmf,0.0);
        Arrays.fill(cdf,0.0);
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
}
