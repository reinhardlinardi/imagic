package com.imagic.imagic;

import android.graphics.Bitmap;

/**
 * A class representing an image.
 */
class Image {

    /* Constants */
    static final String MIME_TYPE = "image/*";

    /* Properties */
    Bitmap bitmap;

    /* Methods */

    // Constructor
    Image() {}

    // Recycle bitmap
    private void recycleBitmap() {
        if(bitmap != null) {
            if(!bitmap.isRecycled()) bitmap.recycle();
        }
    }
}
