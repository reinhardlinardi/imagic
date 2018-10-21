package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * A class representing an image.
 */
class Image {

    /* Constants */
    static final String MIME_TYPE = "image/*";

    /* Properties */
    Bitmap bitmap;

    /* Methods */

    // Constructors
    Image() { recycleBitmap(); }

    Image(Context context, Uri uri) throws Exception {
        recycleBitmap();
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    Image(Context context, Image image) {
        recycleBitmap();
        bitmap = image.bitmap;
    }

    // Recycle bitmap
    private void recycleBitmap() {
        if(bitmap != null) {
            if(!bitmap.isRecycled()) bitmap.recycle();
        }
    }
}
