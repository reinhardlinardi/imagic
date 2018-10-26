package com.imagic.imagic;

/**
 * A class representing RGB histogram by grouping one RedHistogram, one GreenHistogram, and one BlueHistogram.
 * An image is consisted of RGB components, so any operation on one component is very likely to be applied to the others.
 * By providing operations to all RGB components, this class will prevent loops for any RGB operation later.
 */
class RGBHistogram {

    /* Properties */

    RedHistogram red;
    GreenHistogram green;
    BlueHistogram blue;

    /* Methods */

    // Constructor
    RGBHistogram() { resetData(); }

    RGBHistogram(RGBHistogram rgbHistogram) {
        resetData();

        red = new RedHistogram(rgbHistogram.red);
        green = new GreenHistogram(rgbHistogram.green);
        blue = new BlueHistogram(rgbHistogram.blue);
    }

    // Check if all histogram has data
    boolean allHasData() { return !(red.isEmpty() || green.isEmpty() || blue.isEmpty()); }

    // Reset all histogram data
    void resetData() {
        red = new RedHistogram();
        green = new GreenHistogram();
        blue = new BlueHistogram();
    }

    // Stretch histogram, return all mapping
    int[][] stretchHistogram(String algorithm, double redMultiplier, double greenMultiplier, double blueMultiplier) {
        int[][] mapping = new int[3][];

        if(allHasData()) {
            switch(algorithm) {
                case "Linear":
                    mapping[0] = red.linearStretch(redMultiplier);
                    mapping[1] = green.linearStretch(greenMultiplier);
                    mapping[2] = blue.linearStretch(blueMultiplier);
                case "CDF":
                    mapping[0] = red.cdfStretch(redMultiplier);
                    mapping[1] = green.cdfStretch(greenMultiplier);
                    mapping[2] = blue.cdfStretch(blueMultiplier);
                case "Logarithmic":
                    mapping[0] = red.logarithmicStretch(redMultiplier);
                    mapping[1] = green.logarithmicStretch(greenMultiplier);
                    mapping[2] = blue.logarithmicStretch(blueMultiplier);
                default:
                    break;
            }
        }

        return mapping;
    }

    // Histogram matching, return all mapping
    int[][] matchHistogram(double[] cdf) {
        int[][] mapping = new int[3][];

        mapping[0] = red.match(cdf);
        mapping[1] = green.match(cdf);
        mapping[2] = blue.match(cdf);

        return mapping;
    }
}
