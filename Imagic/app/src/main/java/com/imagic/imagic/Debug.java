package com.imagic.imagic;

import android.util.Log;

/**
 * A helper class for printing debugging messages.
 */
class Debug {

    /* Constants */
    private static final String APP_NAME = "Imagic";
    private static final String EXCEPTION_MSG = "Exception";

    /* Methods */

    // Print debug with delimiter (Log.d equivalent)
    static void d(String tag, String delimiter, Object... messages) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for(Object msg : messages) {
            if(!first) stringBuilder.append(delimiter);
            else first = false;

            stringBuilder.append(msg.toString());
        }

        Log.d(tag, stringBuilder.toString());
    }

    // Print error (Log.e equivalent for exceptions)
    static void ex(Exception e) { Log.e(Debug.APP_NAME, Debug.EXCEPTION_MSG, e); }
}
