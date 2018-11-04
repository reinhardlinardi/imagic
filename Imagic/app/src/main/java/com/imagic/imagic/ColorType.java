package com.imagic.imagic;

/**
 * An enum representing color types.
 */
enum ColorType {

    /* Values */
    RED(0), GREEN(1), BLUE(2), GRAYSCALE(3), BLACKWHITE(4);

    /* Properties */
    int value;

    /* Methods */

    // Constructor
    ColorType(int value) { this.value = value; }
}
