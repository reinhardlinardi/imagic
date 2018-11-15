package com.imagic.imagic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * A class representing an image.
 */
class Image {

    /* Constants */
    static final String MIME_TYPE = "image/*";
    private static final int LOLLIPOP_SDK_INT = 24;

    /* Custom kernel */
    double[][][] customKernel;

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

    // Convert image to black and white
    void convertToBlackAndWhite(int blackThreshold) {
        if(hasBitmap()) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];
                    int grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

                    pixels[row * width + col] = (grayscale <= blackThreshold)? BLACK : WHITE;
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }

    // Convert image to grayscale
    void convertToGrayscale() {
        if(hasBitmap()) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];
                    int grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

                    pixels[row * width + col] = Color.rgb(grayscale, grayscale, grayscale);
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
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

    // Is (row, col) outside image bitmap bounds
    private boolean isOutsideImageBitmap(int row, int col) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        return (row < 0 || row >= height || col < 0 || col >= width);
    }

    void applySpecialEffect(Context context, String algorithm, double[][][] customKernel) {
        ConvolutionOperator operator = ConvolutionOperator.getConvolutionOperator(algorithm);
        String kernelJSON = "";
        if(!(operator == ConvolutionOperator.MEDIAN || operator == ConvolutionOperator.DIFFERENCE || operator == ConvolutionOperator.HOMOGENOUS_DIFFERENCE)) {
            try {
                kernelJSON = TextFile.readRawResourceFile(context, R.raw.convolution_kernels);
            } catch(Exception e) {
                Debug.ex(e);
            }
        }
        Bitmap dummyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        applySpecialEffect(operator, customKernel, kernelJSON, dummyBitmap, true);
    }

    // Apply special effect by convolution using specified algorithm
    void applySpecialEffect(ConvolutionOperator operator, double[][][] customKernel, String kernelJson, Bitmap outBitmap, boolean applyChange) {
        if(hasBitmap()) {
//            ConvolutionOperator operator = ConvolutionOperator.getConvolutionOperator(algorithm);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            int[] newPixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            // Row and col offsets for all 9 pixels
            int[][] rowOffset = new int[][]{{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
            int[][] colOffset = new int[][]{{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};

            // Convolution

            // Operators without kernel
            if(operator == ConvolutionOperator.MEDIAN || operator == ConvolutionOperator.DIFFERENCE || operator == ConvolutionOperator.HOMOGENOUS_DIFFERENCE) {
                switch(operator) {
                    case MEDIAN:
                        int[] redValues = new int[9];
                        int[] greenValues = new int[9];
                        int[] blueValues = new int[9];

                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                for(int offsetRow = 0; offsetRow < 3; offsetRow++) {
                                    for(int offsetCol = 0; offsetCol < 3; offsetCol++) {
                                        // Get neighbor
                                        int neighborRow = row + rowOffset[offsetRow][offsetCol];
                                        int neighborCol = col + colOffset[offsetRow][offsetCol];

                                        // If outside grid, use padding value (zero)
                                        if(isOutsideImageBitmap(neighborRow, neighborCol)) {
                                            redValues[offsetRow * 3 + offsetCol] = 0;
                                            greenValues[offsetRow * 3 + offsetCol] = 0;
                                            blueValues[offsetRow * 3 + offsetCol] = 0;
                                        }
                                        else {
                                            redValues[offsetRow * 3 + offsetCol] = Color.red(pixels[neighborRow * width + neighborCol]);
                                            greenValues[offsetRow * 3 + offsetCol] = Color.green(pixels[neighborRow * width + neighborCol]);
                                            blueValues[offsetRow * 3 + offsetCol] = Color.blue(pixels[neighborRow * width + neighborCol]);
                                        }
                                    }
                                }

                                // Sort and take median (5th element)
                                Arrays.sort(redValues);
                                Arrays.sort(greenValues);
                                Arrays.sort(blueValues);

                                newPixels[row * width + col] = Color.rgb(redValues[4], greenValues[4], blueValues[4]);
                            }
                        }
                        break;
                    case DIFFERENCE:
                        convertToGrayscale();

                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                int diff = 0;

                                for(int offsetRow = 0; offsetRow < 3; offsetRow++) {
                                    for(int offsetCol = 0; offsetCol < 3; offsetCol++) {
                                        // If not the element itself
                                        if(!(offsetRow == 1 && offsetCol == 1)) {
                                            // Get two opposite neighbors
                                            int firstNeighborRow = row + rowOffset[offsetRow][offsetCol];
                                            int firstNeighborCol = col + colOffset[offsetRow][offsetCol];

                                            int secondNeighborRow = row - rowOffset[offsetRow][offsetCol];
                                            int secondNeighborCol = col - rowOffset[offsetRow][offsetCol];

                                            // Get difference, check if any neighbors are outside grid
                                            if(isOutsideImageBitmap(firstNeighborRow, firstNeighborCol) && !isOutsideImageBitmap(secondNeighborRow, secondNeighborCol)) diff = Math.max(diff, Color.red(pixels[secondNeighborRow * width + secondNeighborCol]));
                                            else if(!isOutsideImageBitmap(firstNeighborRow, firstNeighborCol) && isOutsideImageBitmap(secondNeighborRow, secondNeighborCol)) diff = Math.max(diff, Color.red(pixels[firstNeighborRow * width + firstNeighborCol]));
                                            else if(isOutsideImageBitmap(firstNeighborRow, firstNeighborCol) && isOutsideImageBitmap(secondNeighborRow, secondNeighborCol)) diff = Math.max(diff, 0);
                                            else diff = Math.max(diff, Math.abs(Color.red(pixels[firstNeighborRow * width + firstNeighborCol]) - Color.red(pixels[secondNeighborRow * width + secondNeighborCol])));
                                        }
                                        else diff = Math.max(diff, 0);
                                    }
                                }

                                newPixels[row * width + col] = Color.rgb(diff, diff, diff);
                            }
                        }
                        break;
                    case HOMOGENOUS_DIFFERENCE:
                        convertToGrayscale();

                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                int diff = 0;

                                for(int offsetRow = 0; offsetRow < 3; offsetRow++) {
                                    for(int offsetCol = 0; offsetCol < 3; offsetCol++) {
                                        // If not the element itself
                                        if(!(offsetRow == 1 && offsetCol == 1)) {
                                            // Get neighbor
                                            int neighborRow = row + rowOffset[offsetRow][offsetCol];
                                            int neighborCol = col + colOffset[offsetRow][offsetCol];

                                            // If outside grid, use padding value (zero)
                                            if(isOutsideImageBitmap(neighborRow, neighborCol)) diff = Math.max(diff, Color.red(pixels[row * width + col]));
                                            else diff = Math.max(diff, Math.abs(Color.red(pixels[row * width + col]) - Color.red(pixels[neighborRow * width + neighborCol])));
                                        }
                                        else diff = Math.max(diff, 0);
                                    }
                                }

                                newPixels[row * width + col] = Color.rgb(diff, diff, diff);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            // Operators with kernel
            else {
                try {
                    String kernelJSON = kernelJson;
                    ArrayList<ConvolutionKernel> kernelList = JSONSerializer.arrayListDeserialize(kernelJSON, ConvolutionKernel.class);

                    ConvolutionKernel operatorKernel;

                    if(operator == ConvolutionOperator.CUSTOM_KERNEL) operatorKernel = new ConvolutionKernel("Custom Kernel", customKernel);
                    else {
                        int idx = 0;

                        for(ConvolutionKernel kernel : kernelList) {
                            if(operator == ConvolutionOperator.getConvolutionOperator(kernel.name)) break;
                            idx++;
                        }

                        operatorKernel = kernelList.get(idx);
                    }

                    int layers = (operator == ConvolutionOperator.FREI_CHEN)? 4 : operatorKernel.kernel.length;

                    if(operator != ConvolutionOperator.MEAN_BLUR) {
                        convertToGrayscale();

                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                double sumOfSquare = 0;

                                for(int layer = 0; layer < layers; layer++) {
                                    double sum = 0;

                                    for(int kernelRow = 0; kernelRow < operatorKernel.kernel[layer].length; kernelRow++) {
                                        for(int kernelCol = 0; kernelCol < operatorKernel.kernel[layer][kernelRow].length; kernelCol++) {
                                            double pixelValue = (isOutsideImageBitmap(row - 1 + kernelRow, col - 1 + kernelCol))? 0 : Color.red(pixels[(row - 1 + kernelRow) * width + (col - 1 + kernelCol)]);
                                            sum += operatorKernel.kernel[layer][kernelRow][kernelCol] * pixelValue;
                                        }
                                    }
                                    sumOfSquare += (sum * sum);
                                }

                                int result = (int) Math.sqrt(sumOfSquare);
                                result = (result < 0)? 0 : (result > 255)? 255 : result;

                                newPixels[row * width + col] = Color.rgb(result, result, result);
                            }
                        }
                    }
                    else {
                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                double redSumOfSquare = 0;
                                double greenSumOfSquare = 0;
                                double blueSumOfSquare = 0;

                                for(int layer = 0; layer < layers; layer++) {
                                    double redSum = 0;
                                    double greenSum = 0;
                                    double blueSum = 0;

                                    for(int kernelRow = 0; kernelRow < 3; kernelRow++) {
                                        for(int kernelCol = 0; kernelCol < 3; kernelCol++) {
                                            double redPixelValue = (isOutsideImageBitmap(row - 1 + kernelRow, col - 1 + kernelCol))? 0 : Color.red(pixels[(row - 1 + kernelRow) * width + (col - 1 + kernelCol)]);
                                            double greenPixelValue = (isOutsideImageBitmap(row - 1 + kernelRow, col - 1 + kernelCol))? 0 : Color.green(pixels[(row - 1 + kernelRow) * width + (col - 1 + kernelCol)]);
                                            double bluePixelValue = (isOutsideImageBitmap(row - 1 + kernelRow, col - 1 + kernelCol))? 0 : Color.blue(pixels[(row - 1 + kernelRow) * width + (col - 1 + kernelCol)]);

                                            redSum += operatorKernel.kernel[layer][kernelRow][kernelCol] * redPixelValue;
                                            greenSum += operatorKernel.kernel[layer][kernelRow][kernelCol] * greenPixelValue;
                                            blueSum += operatorKernel.kernel[layer][kernelRow][kernelCol] * bluePixelValue;
                                        }
                                    }

                                    redSumOfSquare += (redSum * redSum);
                                    greenSumOfSquare += (greenSum * greenSum);
                                    blueSumOfSquare += (blueSum * blueSum);
                                }

                                int redResult = (int) Math.sqrt(redSumOfSquare);
                                int greenResult = (int) Math.sqrt(greenSumOfSquare);
                                int blueResult = (int) Math.sqrt(blueSumOfSquare);

                                redResult = (redResult < 0)? 0 : (redResult > 255)? 255 : redResult;
                                greenResult = (greenResult < 0)? 0 : (greenResult > 255)? 255 : greenResult;
                                blueResult = (blueResult < 0)? 0 : (blueResult > 255)? 255 : blueResult;

                                newPixels[row * width + col] = Color.rgb(redResult, greenResult, blueResult);
                            }
                        }
                    }
                }
                catch(Exception e) {
                    Debug.ex(e);
                }
            }

            if(applyChange) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
            } else {
                outBitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
            }
        }
    }

    public void findFace(Context context){
        if(hasBitmap()) {
            Face face = new Face();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            int[] facePixels = new int[width*height];

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];
//                    int grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                    if(isFace(Color.red(pixel),Color.green(pixel),Color.blue(pixel))){
                        facePixels[row * width + col] = Color.rgb(255, 255, 255);

                    } else {
                        facePixels[row * width + col] = Color.rgb(0, 0, 0);
                    }
                }
            }

            //TODO RESTORE
            findFaceBorder(facePixels,width,height,face);
            Log.d("FACE VALUE", Arrays.toString(face.faceBorder));
            cleanUpNonFaceRegion(facePixels, face);
            Log.d("FACE VALUE", Arrays.toString(face.faceBorder));

            //Find MidPoint
            int faceWidth = face.faceBorder[3].x - face.faceBorder[2].x;
            int faceHeight = face.faceBorder[1].y - face.faceBorder[0].y;
            int faceMidY = face.faceBorder[0].y + (int) ((double) (faceHeight) / 2.0);
            int faceMidX = face.faceBorder[2].x + (int) ((double) (faceWidth) / 2.0);
            Log.d("Face Mid", Integer.toString(faceMidX) + " " + Integer.toString(faceMidY));
            Log.d("Picture Size", Integer.toString(width) + " " + Integer.toString(height));
            Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            int[] outlinePixels = new int[width * height];
            String kernelJSON = "";
            try {
                kernelJSON = TextFile.readRawResourceFile(context, R.raw.convolution_kernels);
            } catch (Exception e) {
                Debug.ex(e); // do nothing
            }
            applySpecialEffect(ConvolutionOperator.SOBEL, customKernel, kernelJSON, outBitmap, false);
            outBitmap.getPixels(outlinePixels, 0, width, 0, 0, width, height);

            //TODO Find Mouth
            int[] horizontalWhiteHistogram = new int[width];
            int[] verticalWhiteHistogram = new int[height];
            for(int row = faceMidY; row <= face.faceBorder[1].y; row++) {
                for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
                    int pixel = outlinePixels[row * width + col];
                    int colorPixel = pixels[row * width + col];
//                    System.out.print(Integer.toString(Color.red(pixel)) + " ");
                    if(Color.red(pixel) > 60) {
                        horizontalWhiteHistogram[col]++;
                        verticalWhiteHistogram[row]++;
                    }
                }
            }

            int maxRowLength = -1;
            int maxColLength = -1;
            int startRowMax = 0;
            int endRowMax = 0;
            int startColMax = 0;
            int endColMax = 0;
            int countRow = 0;
            int countCol = 0;
            int numberOfWhiteThreshold = 18;

//            for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
//                if(horizontalWhiteHistogram[col] >= numberOfWhiteThreshold) {
//                    if (countCol == 0) {
//                        startColMax = col;
//                    }
//                    countCol++;
//                } else {
//                    if(countCol > maxColLength) {
//                        maxColLength = countCol;
//                        endColMax = startColMax + maxColLength;
//                    }
//                    countCol = 0;
//                }
//            }
//            startColMax = endColMax - maxColLength;
//
            startColMax = faceMidX - (int) (0.25 * faceWidth);
            endColMax = startColMax + (int) (0.5 * faceWidth);

            for(int row = faceMidY; row <= face.faceBorder[1].y; row++) {
                if (verticalWhiteHistogram[row] >= numberOfWhiteThreshold) {
                    startRowMax = row;
                    break;
                }
            }

            for(int row = face.faceBorder[1].y; row >= faceMidY; row--) {
                if (verticalWhiteHistogram[row] >= numberOfWhiteThreshold) {
                    endRowMax = row;
                    break;
                }
            }
//            for(int row = faceMidY; row <= face.faceBorder[1].y; row++) {
//                if(verticalWhiteHistogram[row] >= numberOfWhiteThreshold) {
//                    if (countRow == 0) {
//                        startRowMax = row;
//                    }
//                    countRow++;
//                } else {
//                    if(countRow > maxRowLength) {
//                        maxRowLength = countRow;
//                        endRowMax = startRowMax + maxRowLength;
//                    }
//                    countRow = 0;
//                }
//            }
//            startRowMax = endRowMax - maxRowLength;

            Point[] mouthBoundary = new Point[2];
            mouthBoundary[0] = new Point(startColMax, startRowMax);
            mouthBoundary[1] = new Point(endColMax, endRowMax);

            Log.d("Horizontal MOUTH", Arrays.toString(horizontalWhiteHistogram));
            Log.d("Vertical MOUTH", Arrays.toString(verticalWhiteHistogram));
            Log.d("MOUTH BOUNDARY", Arrays.toString(mouthBoundary));


            //TODO Find Eye
            //RESET Histogram
            for(int row = 0; row < height; row++) {
                verticalWhiteHistogram[row] = 0;
            }
            for(int col = 0; col < width; col++) {
                horizontalWhiteHistogram[col] = 0;
            }

            for(int row = faceMidY; row >= face.faceBorder[0].y; row--) {
                for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
                    int pixel = outlinePixels[row * width + col];
                    int colorPixel = pixels[row * width + col];
//                    System.out.print(Integer.toString(Color.red(pixel)) + " ");
                    if(Color.red(pixel) > 60) {
                        horizontalWhiteHistogram[col]++;
                        verticalWhiteHistogram[row]++;
                    }
                }
            }

            Log.d("Horizontal MOUTH", Arrays.toString(horizontalWhiteHistogram));
            Log.d("Vertical MOUTH", Arrays.toString(verticalWhiteHistogram));

            //Final Touch
            drawFaceBorderPixels(pixels, face, mouthBoundary);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            /* NOTE: change pixels to facePixels to see the black and white version */
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            bitmap.setPixels(outlinePixels, 0, width, 0, 0, width, height);
        }
    }

    void cleanUpNonFaceRegion(int[] facePixels, Face face) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // PLOT VERTICAL + HORIZONTAL HISTOGRAM
        int[] horizontalWhiteHistogram = new int[width];
        int[] verticalWhiteHistogram = new int[height];
        for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
            for(int row = face.faceBorder[1].y; row >= face.faceBorder[0].y; row--) {
                int pixel = facePixels[row * width + col];
                if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255) {
                    horizontalWhiteHistogram[col]++;
                    verticalWhiteHistogram[row]++;
                }
            }
        }

        int noiseThreshold = 25;
        for(int row = face.faceBorder[1].y; row >= face.faceBorder[0].y; row--) {
            for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
                int pixel = facePixels[row * width + col];
                if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255) {
                    if(horizontalWhiteHistogram[col] < noiseThreshold || verticalWhiteHistogram[row] < noiseThreshold) {
                        facePixels[row * width + col] = Color.rgb(0, 0, 0);
                    }
                }
            }
        }

        findFaceBorder(facePixels,width,height,face);
        int faceWidth = face.faceBorder[3].x - face.faceBorder[2].x;
        //TODO DELETE NECK: Need to be improved
        double heightThreshold = 1.15;
        int faceHeight = (int) (heightThreshold * (double) faceWidth);

        for(int row = face.faceBorder[0].y + faceHeight; row <= face.faceBorder[1].y; row++) {
            for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
                int pixel = facePixels[row * width + col];
                if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255) {
                    facePixels[row * width + col] = Color.rgb(0, 0, 0);
                }
            }
        }
        findFaceBorder(facePixels,width,height,face);

        Log.d("Horizontal", Arrays.toString(horizontalWhiteHistogram));
        Log.d("Vertical", Arrays.toString(verticalWhiteHistogram));
    }

    private boolean isFace(int r,int g,int b){
        return (
                r > 95 && g > 40 && b > 20 &&
                (Math.max(Math.max(r,g),b) - Math.min(Math.min(r,g),b))>15 &&
                r>g && r>b && (r-g)>15
                );
    }

    private void findFaceBorder(int[] facePixels,int width, int height,Face face){
        boolean found = false;
        //TODO REFACTOR !!!
        /* Find border */
        Point upper = new Point(0,0);
        Point lower = new Point(0,0);
        Point left = new Point(0,0);
        Point right = new Point(0,0);
        /* upper */
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                if(facePixels[row * width + col] == Color.rgb(255,255,255)){
                    upper.set(col,row);
                    found = true;
                    break;
                }
            }
            if(found)break;
        }
        found = false;

        /* lower */
        for(int row = height-1; row >=0; row--) {
            for(int col = 0; col < width; col++) {
                if(facePixels[row * width + col] == Color.rgb(255,255,255)){
                    lower.set(col,row);
                    found = true;
                    break;
                }
            }
            if(found)break;
        }
        found = false;

        /* left */
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                if(facePixels[row * width + col] == Color.rgb(255,255,255)){
                    left.set(col,row);
                    found = true;
                    break;
                }
            }
            if(found)break;
        }
        found = false;

        /* right */
        for(int col = width-1; col >=0; col--) {
            for(int row = 0; row < height; row++) {
                if(facePixels[row * width + col] == Color.rgb(255,255,255)){
                    right.set(col,row);
                    found = true;
                    break;
                }
            }
            if(found)break;
        }

        /* set borders in face object */
        face.setBorder(upper,lower,left,right);
    }

    void drawFaceBorderPixels (int[] pixels, Face face, Point[] mouthBoundary) {
        /* sets border in pixels */
        int width = bitmap.getWidth();
        for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++){
            pixels[face.faceBorder[0].y * width + col] = Color.rgb(0,0,255);
            pixels[face.faceBorder[1].y * width + col] = Color.rgb(0,0,255);
        }

        for(int row = face.faceBorder[0].y; row <= face.faceBorder[1].y; row++){
            pixels[row * width + face.faceBorder[2].x] = Color.rgb(0,0,255);
            pixels[row * width + face.faceBorder[3].x] = Color.rgb(0,0,255);
        }

        for(int col = mouthBoundary[0].x; col <= mouthBoundary[1].x; col++){
            pixels[mouthBoundary[0].y * width + col] = Color.rgb(255,0,0);
            pixels[mouthBoundary[1].y * width + col] = Color.rgb(255,0,0);
        }

        for(int row = mouthBoundary[0].y; row <= mouthBoundary[1].y; row++){
            pixels[row * width + mouthBoundary[0].x] = Color.rgb(255,0,0);
            pixels[row * width + mouthBoundary[1].x] = Color.rgb(255,0,0);
        }
    }
}
