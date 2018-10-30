package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A class representing an image.
 */
class Image {

    /* Constants */
    static final String MIME_TYPE = "image/*";
    private static final int LOLLIPOP_SDK_INT = 24;

    /* Filter Code */
    private static final int MEDIAN = 0;
    private static final int DIFFERENCE = 1;
    private static final int HOMOGENOUS_DIFFERENCE = 2;
    private static final int MEAN_BLUR = 3;
    private static final int SOBEL = 4;
    private static final int PREWITT = 5;
    private static final int ROBERT = 6;
    private static final int FREI_CHEN = 7;

    /* Properties */
    Bitmap bitmap;

    /* Methods */

    // Constructors
    Image() {
        recycleBitmap();
        bitmap = null;
    }

    Image(Context context, Uri uri, int viewWidth, int viewHeight) throws Exception {
        recycleBitmap();

        // Get original image dimension
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        Rect rect = new Rect();
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), rect, bitmapOptions);

        int width = bitmapOptions.outWidth;
        int height = bitmapOptions.outHeight;
        int scaleFactor;

        // If at least one of image dimension is larger than view dimension, scale it down
        if(width > viewWidth || height > viewHeight) scaleFactor = Math.min(width/viewWidth, height/viewHeight);
        else scaleFactor = 1;

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;

        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), rect, bitmapOptions);
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        // Rotate bitmap, so bitmap have correct orientation (if API level >= 24)
        if(Build.VERSION.SDK_INT >= LOLLIPOP_SDK_INT) bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, getRotationMatrix(context, uri), true);
    }

    Image(Context context, Image image) {
        recycleBitmap();
        bitmap = Bitmap.createBitmap(image.bitmap);
    }

    // Check if image has bitmap
    boolean hasBitmap() { return bitmap != null; }

    // Recycle bitmap
    private void recycleBitmap() {
        if(hasBitmap()) {
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
            case ExifInterface.ORIENTATION_ROTATE_90 : degree = 90; break;
            case ExifInterface.ORIENTATION_ROTATE_180 : degree = 180; break;
            case ExifInterface.ORIENTATION_ROTATE_270 : degree = 270 ; break;
            default : degree = 0; break;
        }

        if(rotation != 0f) matrix.preRotate(degree);
        return matrix;
    }

    // Get histogram data by color type
    int[] generateHistogramDataByColorType(ColorType colorType) {
        if(hasBitmap()) {
            int[] frequencyCount = new int[ColorHistogram.NUM_OF_VALUE];
            Arrays.fill(frequencyCount, 0);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];

                    switch (colorType) {
                        case RED: frequencyCount[Color.red(pixel)]++; break;
                        case GREEN: frequencyCount[Color.green(pixel)]++; break;
                        case BLUE: frequencyCount[Color.blue(pixel)]++; break;
                        case GRAYSCALE: frequencyCount[(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3]++; break;
                        default: break;
                    }
                }
            }

            return frequencyCount;
        }
        else return null;
    }

    // Update bitmap by color mapping
    void updateBitmapByColorMapping(int[] redMapping, int[] greenMapping, int[] blueMapping) {
        if(hasBitmap()) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];
                    pixels[row * width + col] = Color.rgb(redMapping[Color.red(pixel)], greenMapping[Color.green(pixel)], blueMapping[Color.blue(pixel)]);
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }

    void convoluteBitmap(String operatorStringCode) {
        //map operatorStringCode to operatorCode
        int operatorCode = mapOperatorCode(operatorStringCode);
        Log.d("operator", Integer.toString(operatorCode));
        // original image size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // padding (image size = (height + 2) * (width + 2)
        // TODO Change to better padding method
        int[][] imageBitmap = new int[height + 2][width + 2];
        int[][] observedPointsRed = new int[3][3];
        int[][] observedPointsGreen = new int[3][3];
        int[][] observedPointsBlue = new int[3][3];

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                imageBitmap[row + 1][col + 1] = pixels[row * width + col];
            }
        }

        // convolution
        for(int row = 1; row < height + 1; row++) {
            for(int col = 1; col < width + 1; col++) {
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        observedPointsRed[i][j] = Color.red(imageBitmap[row - 1 + i][col - 1 + j]);
                        observedPointsGreen[i][j] = Color.green(imageBitmap[row - 1 + i][col - 1 + j]);
                        observedPointsBlue[i][j] = Color.blue(imageBitmap[row - 1 + i][col - 1 + j]);
                    }
                }
                // update pixel value
                pixels[(row - 1) * width + (col - 1)] = Color.rgb(computeNewColorValue(observedPointsRed, operatorCode),
                        computeNewColorValue(observedPointsGreen, operatorCode),
                        computeNewColorValue(observedPointsBlue, operatorCode));
//                Log.d("pixel", Integer.toString(Color.red(pixels[(row - 1) * width + (col - 1)])));
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    int computeNewColorValue(int[][] observedPoints, int operatorCode) {
        int result = 0;
        if (operatorCode == MEDIAN) {
            ArrayList<Integer> valueList = new ArrayList<>();
            for(int i = 0; i < observedPoints.length; i++) {
                for(int j = 0; j < observedPoints[0].length; j++) {
                    valueList.add(observedPoints[i][j]);
                }
            }
            Collections.sort(valueList);
            if(valueList.size() % 2 == 0) {
                result = (valueList.get(valueList.size() / 2 - 1) + valueList.get(valueList.size() / 2)) / 2;
            } else {
                result = valueList.get(valueList.size() / 2);
            }
        } else if (operatorCode == DIFFERENCE) {
            int max = -1;
            int j = observedPoints[0].length - 1;
            for(int i = 0; i < observedPoints[0].length; i++) {
                int diff = Math.abs(observedPoints[0][i] - observedPoints[observedPoints.length - 1][j]);
                if (diff > max) {
                    max = diff;
                }
                j--;
            }

            j = observedPoints.length - 1;
            for(int i = 0; i < observedPoints.length; i++) {
                int diff = Math.abs(observedPoints[i][0] - observedPoints[j][observedPoints[0].length - 1]);
                if (diff > max) {
                    max = diff;
                }
                j--;
            }

            result = max;
        } else if (operatorCode == HOMOGENOUS_DIFFERENCE) {
            // observedPoints dimention must be odd num x odd num
            int center = observedPoints[observedPoints.length / 2][observedPoints[0].length / 2];

            int max = -1;
            for(int i = 0; i < observedPoints[0].length; i++) {
                int diff = Math.abs(observedPoints[0][i] - center);
                if (diff > max) {
                    max = diff;
                }

                diff = Math.abs(observedPoints[observedPoints.length - 1][i] - center);
                if (diff > max) {
                    max = diff;
                }
            }

            for(int i = 0; i < observedPoints.length; i++) {
                int diff = Math.abs(observedPoints[i][0] - center);
                if (diff > max) {
                    max = diff;
                }

                diff = Math.abs(observedPoints[i][observedPoints[0].length - 1] - center);
                if (diff > max) {
                    max = diff;
                }
            }

            result = max;
        }

        if (result < 0) {
            result = 0;
        } else if (result > 255) {
            result = 255;
        }

        return result;
    }

    int mapOperatorCode(String operatorStringCode) {
        int operatorCode = 0;
        switch(operatorStringCode) {
            case "Median":
                operatorCode = MEDIAN;
                break;
            case "Blur (Mean)":
                operatorCode = MEAN_BLUR;
                break;
            case "Difference":
                operatorCode = DIFFERENCE;
                break;
            case "Homogeneous":
                operatorCode = HOMOGENOUS_DIFFERENCE;
                break;
            case "Sobel":
                operatorCode = SOBEL;
                break;
            case "Prewitt":
                operatorCode = PREWITT;
                break;
            case "Robert":
                operatorCode = ROBERT;
                break;
            case "Frei-Chen":
                operatorCode = FREI_CHEN;
                break;
            default:
                break;
        }
        return operatorCode;
    }
}
