package com.imagic.imagic;

/**
 * An enum representing intent request codes.
 */
enum IntentRequestCode {

    /* Values */
    SELECT_IMAGE(0), CAPTURE_IMAGE(1);

    /* Properties */
    int code;

    /* Methods */

    // Constructor
    IntentRequestCode(int code) { this.code = code; }
}
