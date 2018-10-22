package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.Arrays;

/**
 * A class representing an image.
 */
class Image {

    /* Constants */
    static final String MIME_TYPE = "image/*";
    private static final int LOLLIPOP_SDK_INT = 24;

    /* Properties */
    Bitmap bitmap;

    /* Methods */

    // Constructors
    Image() {
        recycleBitmap();
        bitmap = null;
    }

    Image(Context context, Uri uri) throws Exception {
        recycleBitmap();
        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Rotate bitmap, so bitmap have correct orientation
        if(Build.VERSION.SDK_INT >= LOLLIPOP_SDK_INT) bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, getRotationMatrix(context, uri), true);
    }

    Image(Context context, Image image) {
        recycleBitmap();
        bitmap = image.bitmap;
    }

    // Check if image has bitmap
    boolean hasBitmap() { return bitmap != null; }

    // Recycle bitmap
    private void recycleBitmap() {
        if(bitmap != null) {
            if(!bitmap.isRecycled()) bitmap.recycle();
        }
    }

    // Get rotation matrix for bitmap based on original image's orientation
    private Matrix getRotationMatrix(Context context, Uri uri) throws Exception {
        Matrix matrix = new Matrix();
        ExifInterface exif = new ExifInterface(context.getContentResolver().openInputStream(uri));

        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int degree;

        switch(rotation) {
            case ExifInterface.ORIENTATION_ROTATE_90: degree = 90; break;
            case ExifInterface.ORIENTATION_ROTATE_180: degree = 180; break;
            case ExifInterface.ORIENTATION_ROTATE_270: degree = 270 ; break;
            default: degree = 0; break;
        }

        if(rotation != 0f) matrix.preRotate(degree);
        return matrix;
    }

    // Get histogram data by color type
    int[] generateHistogramDataByColorType(ColorType colorType) {
        int[] frequencyCount = new int[ColorHistogram.NUM_OF_VALUE];
        Arrays.fill(frequencyCount, 0);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = pixels[row * width + col];

                switch(colorType) {
                    case RED : frequencyCount[Color.red(pixel)]++; break;
                    case GREEN : frequencyCount[Color.green(pixel)]++; break;
                    case BLUE : frequencyCount[Color.blue(pixel)]++; break;
                    case GRAYSCALE : frequencyCount[(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3]++; break;
                    default : break;
                }
            }
        }

        return frequencyCount;
    }
}
