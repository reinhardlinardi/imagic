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

    /*
    int[][][] kernelInteger = new int[0][0][0];
    double[][][] kernelDouble = new double[0][0][0];
    int kernelValueType = 0; // 0: Integer, 1: Double

    switch(operator) {
        case SOBEL:
            kernelInteger = Kernels.sobel;
            break;
        case PREWITT:
            kernelInteger = Kernels.prewitt;
            break;
        case ROBERT:
            kernelInteger = Kernels.robert;
            break;
        case FREI_CHEN:
            kernelValueType = 1;
            kernelDouble = Kernels.freiChen;
            break;
        case MEAN_BLUR:
            kernelValueType = 1;
            kernelDouble = Kernels.meanBlur;
            break;
        case CUSTOM_KERNEL:
            kernelValueType = 1;
            kernelDouble = this.customKernel;
            break;
        default:
            break;
    }

    if (kernelValueType == 0) { // Integer
        int sumOfSquare = 0;
        for(int i = 0; i < kernelInteger.length; i++) {
            int sum = 0;
            for(int row = 0; row < kernelInteger[i].length; row++) {
                for(int col = 0; col < kernelInteger[i][row].length; col++) {
                    sum += kernelInteger[i][row][col] * observedPoints[row][col];
                }
            }
            sumOfSquare += (sum * sum);
        }
        result = (int) Math.sqrt((double) sumOfSquare);
    }
    else { // Double
        int maxKernelIndex = (operator == ConvolutionOperator.FREI_CHEN) ? 4 : kernelDouble.length;
        double sumOfSquare = 0;
        for(int i = 0; i < maxKernelIndex; i++) {
            double sum = 0;
            for(int row = 0; row < kernelDouble[i].length; row++) {
                for(int col = 0; col < kernelDouble[i][row].length; col++) {
                    sum += kernelDouble[i][row][col] * (double) observedPoints[row][col];
                }
            }
            sumOfSquare += (sum * sum);
        }
        result = (int) Math.sqrt(sumOfSquare);
    }
    */


    // Is (row, col) outside image bitmap bounds
    private boolean isOutsideImageBitmap(int row, int col) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        return (row < 0 || row >= height || col < 0 || col >= width);
    }

    // Apply special effect by convolution using specified algorithm
    void applySpecialEffect(Context context, String algorithm, double[][] customKernel) {
        if(hasBitmap()) {
            ConvolutionOperator operator = ConvolutionOperator.getConvolutionOperator(algorithm);

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
                        int[] values = new int[9];

                        for(int row = 0; row < height; row++) {
                            for(int col = 0; col < width; col++) {
                                for(int offsetRow = 0; offsetRow < 3; offsetRow++) {
                                    for(int offsetCol = 0; offsetCol < 3; offsetCol++) {
                                        // Get neighbor
                                        int neighborRow = row + rowOffset[offsetRow][offsetCol];
                                        int neighborCol = col + colOffset[offsetRow][offsetCol];

                                        // If outside grid, use padding value (zero)
                                        if(isOutsideImageBitmap(neighborRow, neighborCol)) values[offsetRow * 3 + offsetCol] = 0;
                                        else values[offsetRow * 3 + offsetCol] = Color.red(pixels[neighborRow * width + neighborCol]);
                                    }
                                }

                                // Sort and take median (5th element)
                                Arrays.sort(values);
                                newPixels[row * width + col] = Color.rgb(values[4], values[4], values[4]);
                            }
                        }
                        break;
                    case DIFFERENCE:
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
                    String kernelJSON = TextFile.readRawResourceFile(context, R.raw.convolution_kernels);
                    ArrayList<ConvolutionKernel> kernelList = JSONSerializer.arrayListDeserialize(kernelJSON, ConvolutionKernel.class);

                    int idx = 0;

                    for(ConvolutionKernel kernel : kernelList) {
                        if(operator == ConvolutionOperator.getConvolutionOperator(kernel.name)) break;
                        idx++;
                    }

                    ConvolutionKernel operatorKernel = kernelList.get(idx);
                    int layers = (operator == ConvolutionOperator.FREI_CHEN)? 4 : operatorKernel.kernel.length;

                    for(int row = 0; row < height; row++) {
                        for(int col = 0; col < width; col++) {
                            double sumOfSquare = 0;

                            for(int layer = 0; layer < layers; layer++) {
                                double sum = 0;

                                for(int kernelRow = 0; kernelRow < operatorKernel.kernel[layer].length; kernelRow++) {
                                    for(int kernelCol = 0; kernelCol < operatorKernel.kernel[layer][kernelRow].length; kernelCol++) {
                                        double pixelValue = (isOutsideImageBitmap(row - 1 + kernelRow, col - 1 + kernelCol))? 0 : pixels[(row - 1 + kernelRow) * width + (col - 1 + kernelCol)];
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
                catch(Exception e) {
                    Debug.ex(e);
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        }
    }
}
