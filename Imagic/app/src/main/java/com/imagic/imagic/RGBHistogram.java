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

    // Check if all histogram has data
    boolean allHasData() { return !(red.isEmpty() || green.isEmpty() || blue.isEmpty()); }

    // Reset all histogram data
    void resetData() {
        red = new RedHistogram();
        green = new GreenHistogram();
        blue = new BlueHistogram();
    }
}
