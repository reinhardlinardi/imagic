package com.imagic.imagic;

/**
 * An enum representing convolution operator/
 */
enum ConvolutionOperator {

    /* Values */
    MEDIAN(0), MEAN_BLUR(1), DIFFERENCE(2), HOMOGENOUS_DIFFERENCE(3), SOBEL(4), PREWITT(5), ROBERT(6), FREI_CHEN(7), CUSTOM_KERNEL(8);

    /* Properties */
    int value;

    /* Methods */

    // Constructor
    ConvolutionOperator(int value) { this.value = value; }

    // Get convolution operator from string
    static ConvolutionOperator getConvolutionOperator(String algorithm) {
        switch(algorithm) {
            case "Median" : return MEDIAN;
            case "Mean (Blur)" : return MEAN_BLUR;
            case "Difference" : return DIFFERENCE;
            case "Homogeneous" : return HOMOGENOUS_DIFFERENCE;
            case "Sobel" : return SOBEL;
            case "Prewitt" : return PREWITT;
            case "Robert" : return ROBERT;
            case "Frei-Chen" : return FREI_CHEN;
            case "Custom Kernel" : return CUSTOM_KERNEL;
            default : return null;
        }
    }
}
