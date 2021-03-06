package com.imagic.imagic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
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

    private final int MATRIX_WHITE = 0;
    private final int MATRIX_BLACK = 1;

    private final int MATRIX_VERTEX_GREEN = 2;
    private final int MATRIX_INTERSECTION_BLUE = 3;

    /* Custom kernel */
    double[][][] customKernel;

    /* Matrix of bitmap */
    int[][] bitmapMatrix;

    /* Image skeleton */
    Skeleton skeleton;

    /* Properties */
    Bitmap bitmap;

    /* Face recognition things */
    int[][] visitedPixel;
    int[] facePixels;
    int PIXEL_VISITED = 1;
    int[][] neighbor = {{1, 0}, {0, 1}, {-1, 0}, {0, -1},
            {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    int[] tempFaceBoundary;
    int sumOfFaceHeight = 0;
    int sumOfFaceWidth = 0;
    int numOfFace = 0;
    Point[] mouthControlPoints = new Point[16];
    Point[][] eyesControlPoints = new Point[2][10];
    Point[][] eyebrowsControlPoints = new Point[2][7];
    Point[] noseControlPoints = new Point[12];

    //Templates
    double mouthScoreWeight = 1.0;
    double eyesScoreWeight = 1.0;
    double eyebrowsScoreWeight = 1.0;
    double noseScoreWeight = 0.1;
    String[] labels = {"Roland", "Dad", "Mom", "Suhendi"};
//    String[] labels = {"Roland", "Dad", "Mom", "Suhendi", "Agus"};
    double[][] mouthTemplateGradients = new double[labels.length][16];
    double[][][] eyeTemplateGradients = new double[labels.length][2][10];
    double[][][] eyebrowTemplateGradients = new double[labels.length][2][6];
    double[][] noseTemplateGradients = new double[labels.length][12];
    Point[][] mouthCPTemplate = {
        {new Point(179, 372), new Point(192, 372), new Point(205, 379), new Point(218, 372), new Point(230, 372), new Point(242, 374), new Point(254, 373), new Point(266, 383), new Point(277, 377), new Point(266, 394), new Point(254, 398), new Point(242, 406), new Point(230, 406), new Point(218, 400), new Point(205, 392), new Point(192, 389)},
            {new Point(147, 334), new Point(155, 339), new Point(163, 338), new Point(171, 332), new Point(179, 336), new Point(186, 339), new Point(193, 339), new Point(200, 332), new Point(206, 338), new Point(200, 344), new Point(193, 344), new Point(186, 344), new Point(179, 355), new Point(171, 355), new Point(163, 348), new Point(155, 344)},
            {new Point(154, 349), new Point(163, 342), new Point(171, 345), new Point(179, 343), new Point(187, 330), new Point(195, 343), new Point(203, 343), new Point(211, 341), new Point(219, 347), new Point(211, 357), new Point(203, 362), new Point(195, 362), new Point(187, 362), new Point(179, 361), new Point(171, 353), new Point(163, 351)},
            {new Point(71, 223), new Point(80, 220), new Point(89, 215), new Point(97, 219), new Point(105, 211), new Point(113, 213), new Point(121, 214), new Point(129, 218), new Point(136, 222), new Point(129, 227), new Point(121, 231), new Point(113, 227), new Point(105, 239), new Point(97, 234), new Point(89, 234), new Point(80, 228)},
//            {new Point(161, 273), new Point(170, 279), new Point(178, 277), new Point(186, 275), new Point(194, 276), new Point(202, 273), new Point(210, 276), new Point(218, 276), new Point(226, 286), new Point(218, 287), new Point(210, 285), new Point(202, 287), new Point(194, 287), new Point(186, 286), new Point(178, 289), new Point(170, 288)},
    };
    Point[][][] eyesCPTemplate = {
            {
                    {new Point(168, 273), new Point(176, 271), new Point(184, 270), new Point(192, 270), new Point(200, 273), new Point(207, 281), new Point(200, 284), new Point(192, 286), new Point(184, 287), new Point(176, 287)},
                    {new Point(262, 280), new Point(270, 272), new Point(278, 269), new Point(285, 269), new Point(292, 270), new Point(298, 272), new Point(292, 287), new Point(285, 287), new Point(278, 286), new Point(270, 285)}
            },
            {
                    {new Point(135, 268), new Point(140, 265), new Point(145, 267), new Point(150, 265), new Point(155, 267), new Point(159, 276), new Point(155, 276), new Point(150, 279), new Point(145, 279), new Point(140, 273)},
                    {new Point(194, 275), new Point(200, 266), new Point(206, 265), new Point(211, 266), new Point(216, 269), new Point(220, 273), new Point(216, 278), new Point(211, 274), new Point(206, 280), new Point(200, 279)}
            },
            {
                    {new Point(138, 264), new Point(145, 260), new Point(152, 259), new Point(159, 259), new Point(165, 260), new Point(170, 267), new Point(165, 272), new Point(159, 271), new Point(152, 271), new Point(145, 272)},
                    {new Point(210, 268), new Point(217, 260), new Point(224, 260), new Point(231, 261), new Point(237, 264), new Point(242, 269), new Point(237, 271), new Point(231, 274), new Point(224, 273), new Point(217, 272)}
            },
            {
                    {new Point(58, 141), new Point(64, 139), new Point(70, 139), new Point(76, 140), new Point(82, 142), new Point(87, 149), new Point(82, 143), new Point(76, 154), new Point(70, 153), new Point(64, 147)},
                    {new Point(123, 147), new Point(129, 142), new Point(135, 139), new Point(141, 139), new Point(147, 139), new Point(152, 141), new Point(147, 154), new Point(141, 154), new Point(135, 153), new Point(129, 151)}
            },
//            {
//                    {new Point(149, 203), new Point(154, 193), new Point(159, 193), new Point(164, 193), new Point(169, 205), new Point(173, 208), new Point(169, 211), new Point(164, 212), new Point(159, 212), new Point(154, 212)},
//                    {new Point(221, 197), new Point(226, 197), new Point(231, 198), new Point(236, 198), new Point(241, 201), new Point(245, 203), new Point(241, 209), new Point(236, 209), new Point(231, 203), new Point(226, 212)}
//            },
    };
    Point[][][] eyebrowsCPTemplate = {
            {
                    {new Point(158, 261), new Point(167, 257), new Point(176, 255), new Point(185, 253), new Point(194, 254), new Point(203, 254), new Point(211, 246)},
                    {new Point(255, 248), new Point(264, 255), new Point(273, 254), new Point(282, 253), new Point(291, 254), new Point(300, 257), new Point(308, 260)}
            },
            {
                    {new Point(135, 261), new Point(140, 265), new Point(145, 264), new Point(150, 265), new Point(155, 264), new Point(159, 265), new Point(162, 258)},
                    {new Point(194, 264), new Point(198, 263), new Point(202, 265), new Point(206, 265), new Point(209, 265), new Point(212, 262), new Point(214, 261)}
            },
            {
                    {new Point(130, 249), new Point(137, 242), new Point(144, 253), new Point(151, 252), new Point(158, 253), new Point(165, 255), new Point(170, 254)},
                    {new Point(210, 254), new Point(217, 254), new Point(224, 253), new Point(231, 251), new Point(238, 242), new Point(245, 248), new Point(250, 255)}
            },
            {
                    {new Point(51, 134), new Point(57, 135), new Point(62, 133), new Point(67, 134), new Point(72, 132), new Point(77, 131), new Point(87, 127)},
                    {new Point(123, 131), new Point(129, 130), new Point(134, 132), new Point(139, 132), new Point(144, 131), new Point(149, 133), new Point(154, 133)}
            },
//            {
//                    {new Point(141, 191), new Point(148, 193), new Point(155, 193), new Point(161, 193), new Point(167, 193), new Point(173, 193), new Point(178, 193)},
//                    {new Point(221, 192), new Point(228, 191), new Point(235, 192), new Point(242, 191), new Point(249, 193), new Point(256, 193), new Point(261, 190)}
//            },
    };
    Point[][] noseCPTemplate = {
            {new Point(211, 339), new Point(219, 338), new Point(227, 341), new Point(235, 341), new Point(243, 339), new Point(250, 338), new Point(255, 338), new Point(250, 345), new Point(243, 344), new Point(235, 348), new Point(227, 348), new Point(219, 347)},
            {new Point(162, 311), new Point(167, 311), new Point(172, 313), new Point(177, 313), new Point(182, 311), new Point(187, 319), new Point(191, 315), new Point(187, 321), new Point(182, 319), new Point(177, 321), new Point(172, 322), new Point(167, 320)},
            {new Point(170, 324), new Point(177, 318), new Point(184, 319), new Point(191, 320), new Point(198, 318), new Point(205, 318), new Point(210, 319), new Point(205, 326), new Point(198, 325), new Point(191, 326), new Point(184, 326), new Point(177, 326)},
            {new Point(87, 192), new Point(93, 191), new Point(98, 193), new Point(103, 193), new Point(108, 196), new Point(113, 192), new Point(118, 201), new Point(113, 193), new Point(108, 196), new Point(103, 195), new Point(98, 195), new Point(93, 196)},
//            {new Point(181, 252), new Point(188, 254), new Point(195, 251), new Point(201, 252), new Point(207, 252), new Point(213, 246), new Point(218, 247), new Point(213, 260), new Point(207, 258), new Point(201, 255), new Point(195, 252), new Point(188, 263)},
    };

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

    // Get matrix of bitmap
    void getBitmapMatrix() {
        if(hasBitmap()) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            bitmapMatrix = new int[height][width];

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    int pixel = pixels[row * width + col];
                    bitmapMatrix[row][col] = (pixel == BLACK)? 1 : 0;
                }
            }
        }
    }

    // Set bitmap from matrix
    void setBitmapFromMatrix() {
        if(hasBitmap()) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int [width * height];

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    switch(skeleton.skeletonMatrix[row][col]) {
                        case MATRIX_BLACK:
                            pixels[row * width + col] = Color.rgb(0xFF,0xFF,0xFF);
                            break;
                        case MATRIX_WHITE:
                            pixels[row * width + col] = Color.rgb(0,0,0);
                            break;
                        case MATRIX_VERTEX_GREEN:
                            pixels[row * width + col] = Color.rgb(0x32,0xCD,0x32);
                            break;
                        case MATRIX_INTERSECTION_BLUE:
                            pixels[row * width + col] = Color.rgb(0x00,0xFF,0xFF);
                            break;
                    }
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }

    // Get image skeleton
    void getSkeleton() {
        if(hasBitmap()) {
            skeleton = new Skeleton(this.bitmapMatrix);
            skeleton.postProcess();
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

    public void findFace(Context context, String[] identity){
        if(hasBitmap()) {
            calculateAllGradients();
            ArrayList<Face> faces = new ArrayList<Face>();
//            Face face = new Face();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            facePixels = new int[width*height];
            visitedPixel = new int[height][width];

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

            //TODO IN DEVELOPMENT: DETECT MULTIPLE FACES
            faces = findFaceCandidates(width, height);
            Log.d("FACE CANDIDATES", Integer.toString(faces.size()));

//            Log.d("FACE 1", Arrays.toString(faces.get(0).faceBorder));
//            Log.d("FACE 2", Arrays.toString(faces.get(1).faceBorder));
            for(Face face : faces) {
                int faceWidth = face.faceBorder[3].x - face.faceBorder[2].x;
                int faceHeight = face.faceBorder[1].y - face.faceBorder[0].y;
                if (isNoise(faceWidth, faceHeight)) {
//                    Log.d("CONTINUE", "TES");
                    continue;
                } else {
                    Log.d("CONTINUE", Integer.toString(faceWidth) + " " + Integer.toString(faceHeight));
                }

                Log.d("FACE VALUE", Arrays.toString(face.faceBorder));
                face = cleanUpNonFaceRegion(face);
                Log.d("FACE VALUE", Arrays.toString(face.faceBorder));

                //Find MidPoint
                faceWidth = face.faceBorder[3].x - face.faceBorder[2].x;
                faceHeight = face.faceBorder[1].y - face.faceBorder[0].y;

                //SECOND SIZE FILTER
                if (isNoise(faceWidth, faceHeight)) {
                    continue;
                } else {
                    Log.d("CONTINUE", Integer.toString(faceWidth) + " " + Integer.toString(faceHeight));
                }

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

                int[] horizontalWhiteHistogram = new int[width];
                int[] verticalWhiteHistogram = new int[height];
                Point[] mouthBoundary = new Point[2];
                //TODO Find Mouth
                //TODO NEW METHOD

                int startRow = face.faceBorder[1].y;
                int startColMax = faceMidX - (int) (0.25 * faceWidth);
                int endColMax = startColMax + (int) (0.5 * faceWidth);
                int maxColorValue = -1;
                int maxColorRow = -1;
                int maxColorCol = -1;

                int defaultMouthWidth = (int)(0.45 * (double) faceWidth);
                int defaultMouthHeight = (int)(0.2 * (double) faceHeight);

                int maxScore = -1;
                int maxRow = -1;
                int maxCol = -1;
                int bottomBoundary = (face.faceBorder[1].y + (int) (0.2 * (double)defaultMouthHeight) < width)?
                        face.faceBorder[1].y - defaultMouthHeight: face.faceBorder[1].y - (int) (0.8 * (double)defaultMouthHeight);

                for(int row = faceMidY + (int) (0.12 * (double) faceHeight); row < bottomBoundary; row++) {
                    for(int col = startColMax; col < endColMax - defaultMouthWidth; col++) {
                        for(int rowIn = row; rowIn < row + defaultMouthHeight-1; rowIn++) {
                            verticalWhiteHistogram[rowIn] = 0;
                        }
                        for(int colIn = col; colIn < col + defaultMouthWidth-1; colIn++) {
                            horizontalWhiteHistogram[colIn] = 0;
                        }

                        for(int rowIn = row; rowIn < row + defaultMouthHeight-1; rowIn++) {
                            for(int colIn = col; colIn < col + defaultMouthWidth-1; colIn++) {
                                int pixel = outlinePixels[rowIn * width + colIn];
                                int colorPixel = pixels[rowIn * width + colIn];
                                if(Color.red(pixel) > 45) {
                                    horizontalWhiteHistogram[colIn]++;
                                    verticalWhiteHistogram[rowIn]++;
                                }
                            }
                        }

                        int score = 0;
                        double mouthWidthEstimation = 0.8;
                        for(int rowIn = row + (int) (0.5 * (double) defaultMouthHeight); rowIn < row + defaultMouthHeight-1; rowIn++) {
                            if(verticalWhiteHistogram[rowIn] > mouthWidthEstimation * defaultMouthWidth) {
                                score++;
                                mouthWidthEstimation -= 0.08;
                            } else {
                                break;
                            }
                        }

                        mouthWidthEstimation = 0.7;
                        for(int rowIn = row + (int) (0.5 * (double) defaultMouthHeight) - 1; rowIn >= row + (int) (0.2 * (double) defaultMouthHeight); rowIn--) {
                            if(verticalWhiteHistogram[rowIn] > mouthWidthEstimation * defaultMouthWidth) {
                                score++;
                                mouthWidthEstimation -= 0.08;
                            } else {
                                break;
                            }
                        }
                        for(int colIn = col + (int) (0.5 * (double) defaultMouthWidth); colIn < col + defaultMouthWidth-1; colIn++) {
                            if(horizontalWhiteHistogram[colIn] > 0.6 * defaultMouthHeight) {
                                score++;
                            }
                            if(horizontalWhiteHistogram[colIn] < 0.25 * defaultMouthHeight) {
                                break;
                            }
                        }
                        for(int colIn = col + (int) (0.5 * (double) defaultMouthWidth) - 1; colIn >= col; colIn--) {
                            if(horizontalWhiteHistogram[colIn] > 0.6 * defaultMouthHeight) {
                                score++;
                            }
                            if(horizontalWhiteHistogram[colIn] < 0.25 * defaultMouthHeight) {
                                break;
                            }
                        }
                        if(maxScore < score) {
                            maxScore = score;
                            maxRow = row;
                            maxCol = col;
                            Log.d("Horizontal MOUTH", Arrays.toString(horizontalWhiteHistogram));
                            Log.d("Vertical MOUTH", Arrays.toString(verticalWhiteHistogram));
                        }
                    }
                }

                mouthBoundary[0] = new Point(maxCol, maxRow);
                mouthBoundary[1] = new Point(maxCol + defaultMouthWidth, maxRow + defaultMouthHeight);

                Log.d("MOUTH", Arrays.toString(mouthBoundary));
//                Log.d("RIGHT EYE", Arrays.toString(eyeBoundary[1]));

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

                int counter = 0;
                int bottomEyeBoundary = -1;
                int topEyeBoundary = -1;
                for(int row = faceMidY; row >= face.faceBorder[0].y; row--) {
                    if(verticalWhiteHistogram[row] > 50) {
                        counter++;
                        bottomEyeBoundary = (counter == 1)? row:bottomEyeBoundary;
//                    topEyeBoundary = (counter == 2)? row:topEyeBoundary;
                    }
                    if(counter == 1) {
                        break;
                    }
                }
                topEyeBoundary = bottomEyeBoundary - (int)(0.1 * (double) faceHeight);
                if(topEyeBoundary < 0) {
                    continue;
                }

                counter = 0;
                int leftEyeBoundary = -1;
                int rightEyeBoundary = -1;
                for(int col = faceMidX; col <= face.faceBorder[3].x; col++) {
                    if(horizontalWhiteHistogram[col] > 25) {
                        counter++;
                        leftEyeBoundary = (counter == 1)? col:leftEyeBoundary;
//                    rightEyeBoundary = (counter == 2)? col:rightEyeBoundary;
                    }
                    if(counter == 1) {
                        break;
                    }
                }

                double eyeWidthPercentage = 0.2;
                if(leftEyeBoundary < faceMidX + (0.12 * (double) faceWidth)) {
                    leftEyeBoundary = faceMidX + (int) (0.125 * (double) faceWidth);
                }
                rightEyeBoundary = leftEyeBoundary + (int) (eyeWidthPercentage * (double) faceWidth);

                if(rightEyeBoundary > face.faceBorder[3].x - (0.16 * (double) faceWidth)) {
                    rightEyeBoundary = faceMidX + (int) (0.3 * (double) faceWidth);
                    leftEyeBoundary = rightEyeBoundary - (int) (eyeWidthPercentage * (double) faceWidth);
                }

                Log.d("left boundary", Integer.toString(leftEyeBoundary) + " " + Double.toString(eyeWidthPercentage * (double) faceWidth));
                Point[][] eyeBoundary = new Point[2][2];
                eyeBoundary[1][0] = new Point(leftEyeBoundary, topEyeBoundary);
                eyeBoundary[1][1] = new Point(rightEyeBoundary, bottomEyeBoundary);
                eyeBoundary[0][1] = new Point(faceMidX - (leftEyeBoundary - faceMidX), bottomEyeBoundary);
                eyeBoundary[0][0] = new Point(faceMidX - (rightEyeBoundary - faceMidX), topEyeBoundary);
                Log.d("LEFT EYE", Arrays.toString(eyeBoundary[0]));
                Log.d("RIGHT EYE", Arrays.toString(eyeBoundary[1]));

                Log.d("Horizontal EYE", Arrays.toString(horizontalWhiteHistogram));
                Log.d("Vertical EYE", Arrays.toString(verticalWhiteHistogram));

                Point[][] eyebrowBoundary = new Point[2][2];
                Point[] noseBoundary = new Point[2];
                //Final Touch
                drawFaceBorderPixels(pixels, face, mouthBoundary, eyeBoundary, eyebrowBoundary, noseBoundary);
                Log.d("eyebrow left", Arrays.toString(eyebrowBoundary[0]));
                Log.d("eyebrow right", Arrays.toString(eyebrowBoundary[1]));
                Log.d("nose", Arrays.toString(noseBoundary));

                String result = analyzeFace(outlinePixels, face, mouthBoundary, eyeBoundary, eyebrowBoundary, noseBoundary);
                drawControlPoints(pixels);
                Log.d("Identity", result);
                identity[0] = result;
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            /* NOTE: change pixels to facePixels to see the black and white version */
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            bitmap.setPixels(outlinePixels, 0, width, 0, 0, width, height);
//            bitmap.setPixels(facePixels, 0, width, 0, 0, width, height);
        }
    }

    Face cleanUpNonFaceRegion(Face face) {
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

        Log.d("FaceBorders", Arrays.toString(face.faceBorder));
        face = findFaceBorder(width,height,face);
        Log.d("FaceBordersAFTER", Arrays.toString(face.faceBorder));
        int faceWidth = face.faceBorder[3].x - face.faceBorder[2].x;
        //TODO DELETE NECK: Need to be improved
        double heightThreshold = 1.18;
        int faceHeight = (int) (heightThreshold * (double) faceWidth);

        for(int row = face.faceBorder[0].y + faceHeight; row <= face.faceBorder[1].y; row++) {
            for(int col = face.faceBorder[2].x; col <= face.faceBorder[3].x; col++) {
                int pixel = facePixels[row * width + col];
                if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255) {
                    facePixels[row * width + col] = Color.rgb(0, 0, 0);
                }
            }
        }
        face = findFaceBorder(width,height,face);

        Log.d("Horizontal", Arrays.toString(horizontalWhiteHistogram));
        Log.d("Vertical", Arrays.toString(verticalWhiteHistogram));

        return face;
    }

    private boolean isNoise(int faceWidth, int faceHeight) {
        boolean result = true;
        if(faceWidth > 0 && faceHeight > 0) {
            double maxThresholdHeightWidth = 3;
            double minThresholdHeightWidth = 0.85;
            double faceHeightWidthRatio = (double) faceHeight / (double) faceWidth;
            int faceArea = faceHeight * faceWidth;
            int pictureArea = visitedPixel.length * visitedPixel[0].length;
            double areaRatio = (double) faceArea / (double) pictureArea;
            double thresholdArea = 0.01;
//            Log.d("height width ratio", Double.toString(faceHeightWidthRatio));
//            Log.d("area ratio", Double.toString(areaRatio));

            result = (faceHeightWidthRatio > maxThresholdHeightWidth ||
                    faceHeightWidthRatio < minThresholdHeightWidth ||
                    areaRatio < thresholdArea);
        }
        return result;
    }

    private boolean isFace(int r,int g,int b){
        double y, cb, cr;
        y = 0.299 * (double)r + (-0.587) * (double)g + 0.114 * (double)b;
        cb = (-0.168) * (double)r + (-0.331) * (double)g + 0.5 * (double)b + 128.0;
        cr = 0.5 * (double)r + (-0.418) * (double)g + (-0.081) * (double)b + 128.0;

        return (cb > 90.0 && cb < 125.0 &&
                cr > 135.0 && cr < 180.0);
    }

    private ArrayList<Face> findFaceCandidates(int width, int height) {
        ArrayList<Face> faces = new ArrayList<Face>();
        tempFaceBoundary = new int[4]; //UPPER, LOWER, LEFT, RIGHT
        tempFaceBoundary[0] = visitedPixel.length-1;
        tempFaceBoundary[1] = 0;
        tempFaceBoundary[2] = visitedPixel[0].length-1;
        tempFaceBoundary[3] = 0;

        //TODO DFS
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                int pixel = facePixels[row * width + col];
                if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255 &&
                        visitedPixel[row][col] != PIXEL_VISITED) {
                    Face tempFace = new Face();
                    dfsCandidateFace(row, col);
                    tempFace.setBorder(new Point(tempFaceBoundary[2], tempFaceBoundary[0]),
                            new Point(tempFaceBoundary[3], tempFaceBoundary[1]),
                            new Point(tempFaceBoundary[2], tempFaceBoundary[0]),
                            new Point(tempFaceBoundary[3], tempFaceBoundary[1]));
                    faces.add(tempFace);

                    //reset boundary for next face
                    tempFaceBoundary[0] = visitedPixel.length-1;
                    tempFaceBoundary[1] = 0;
                    tempFaceBoundary[2] = visitedPixel[0].length-1;
                    tempFaceBoundary[3] = 0;
                }
            }
        }

        return faces;
    }

    private void dfsCandidateFace(int row, int col) {
        if(row < tempFaceBoundary[0]) {
            tempFaceBoundary[0] = row;
        }
        if(row > tempFaceBoundary[1]) {
            tempFaceBoundary[1] = row;
        }
        if(col < tempFaceBoundary[2]) {
            tempFaceBoundary[2] = col;
        }
        if(col > tempFaceBoundary[3]) {
            tempFaceBoundary[3] = col;
        }
        visitedPixel[row][col] = PIXEL_VISITED;
        for(int i = 0; i < neighbor.length; i++) {
            int pixel = facePixels[row * visitedPixel[0].length + col];
            int nextRow = row + neighbor[i][0];
            int nextCol = col + neighbor[i][1];
            if (nextRow >= 0 && nextRow < visitedPixel.length &&
                    nextCol >= 0 && nextCol < visitedPixel[0].length) {
                if (isLegalPixel(nextRow, nextCol)) {
                    dfsCandidateFace(row + neighbor[i][0], col + neighbor[i][1]);
                }
            }
        }

    }

    private boolean isLegalPixel(int row, int col) {
        boolean isLegalNeighbor = false;
        for(int i = 0; i < 4; i++) {
            int pixel = facePixels[row * visitedPixel[0].length + col];
            int nextRow = row + neighbor[i][0];
            int nextCol = col + neighbor[i][1];
            if (nextRow >= 0 && nextRow < visitedPixel.length &&
                    nextCol >= 0 && nextCol < visitedPixel[0].length) {
                if (isBlack(facePixels[nextRow * visitedPixel[0].length + nextCol])) {
                    isLegalNeighbor = true;
                }
            }
        }
        return (visitedPixel[row][col] != PIXEL_VISITED &&
                isWhite(facePixels[row * visitedPixel[0].length + col]) &&
                isLegalNeighbor);
    }

    private boolean isBlack(int pixel) {
        return (Color.red(pixel) == 0 && Color.green(pixel) == 0 && Color.blue(pixel) == 0);
    }

    private boolean isWhite(int pixel) {
        return (Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255);
    }

    private void resetVisited() {
        for(int i = 0; i < visitedPixel.length; i++) {
            for(int j = 0; j < visitedPixel[0].length; j++) {
                visitedPixel[i][j] = 0;
            }
        }
    }

    private Face findFaceBorder(int width, int height, Face face){
        tempFaceBoundary[0] = visitedPixel.length-1;
        tempFaceBoundary[1] = 0;
        tempFaceBoundary[2] = visitedPixel[0].length-1;
        tempFaceBoundary[3] = 0;

        for(int row = face.faceBorder[0].y; row < face.faceBorder[1].y; row++) {
            for(int col = face.faceBorder[0].x; col < face.faceBorder[1].x; col++) {
                int pixel = facePixels[row * visitedPixel[0].length + col];
                if(isWhite(pixel)) {
                    if(row < tempFaceBoundary[0]) {
                        tempFaceBoundary[0] = row;
                    }
                    if(row > tempFaceBoundary[1]) {
                        tempFaceBoundary[1] = row;
                    }
                    if(col < tempFaceBoundary[2]) {
                        tempFaceBoundary[2] = col;
                    }
                    if(col > tempFaceBoundary[3]) {
                        tempFaceBoundary[3] = col;
                    }
                }
            }
        }

        face.setBorder(new Point(tempFaceBoundary[2], tempFaceBoundary[0]),
                new Point(tempFaceBoundary[3], tempFaceBoundary[1]),
                new Point(tempFaceBoundary[2], tempFaceBoundary[0]),
                new Point(tempFaceBoundary[3], tempFaceBoundary[1]));
        return face;
    }

    void drawFaceBorderPixels (int[] pixels, Face face, Point[] mouthBoundary, Point[][] eyeBoundary, Point[][] eyebrowBoundary, Point[] noseBoundary) {
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

        //EYE
        for(int col = eyeBoundary[0][0].x; col <= eyeBoundary[0][1].x; col++){
            pixels[eyeBoundary[0][0].y * width + col] = Color.rgb(0,255,0);
            pixels[eyeBoundary[0][1].y * width + col] = Color.rgb(0,255,0);
        }

        for(int col = eyeBoundary[1][0].x; col <= eyeBoundary[1][1].x; col++){
            pixels[eyeBoundary[1][0].y * width + col] = Color.rgb(0,255,0);
            pixels[eyeBoundary[1][1].y * width + col] = Color.rgb(0,255,0);
        }

        for(int row = eyeBoundary[0][0].y; row <= eyeBoundary[0][1].y; row++){
            pixels[row * width + eyeBoundary[0][0].x] = Color.rgb(0,255,0);
            pixels[row * width + eyeBoundary[0][1].x] = Color.rgb(0,255,0);
            pixels[row * width + eyeBoundary[1][0].x] = Color.rgb(0,255,0);
            pixels[row * width + eyeBoundary[1][1].x] = Color.rgb(0,255,0);
        }

        //EYELASH
        int eyelashHeight = (int)(0.7*(double)(eyeBoundary[0][1].y - eyeBoundary[0][0].y));
        int eyelashWidth = (int)(1.25*(double)(eyeBoundary[0][1].x - eyeBoundary[0][0].x));
        eyebrowBoundary[0][0] = new Point(eyeBoundary[0][1].x - eyelashWidth, eyeBoundary[0][0].y - eyelashHeight);
        eyebrowBoundary[0][1] = new Point(eyeBoundary[0][1].x, eyeBoundary[0][0].y);
        for(int col = eyeBoundary[0][1].x - eyelashWidth; col <= eyeBoundary[0][1].x; col++){
            pixels[eyeBoundary[0][0].y * width + col] = Color.rgb(255,0,255);
            pixels[(eyeBoundary[0][0].y - eyelashHeight) * width + col] = Color.rgb(255,0,255);
        }

        eyebrowBoundary[1][0] = new Point(eyeBoundary[1][0].x, eyeBoundary[0][0].y - eyelashHeight);
        eyebrowBoundary[1][1] = new Point(eyeBoundary[1][0].x + eyelashWidth, eyeBoundary[0][0].y);
        for(int col = eyeBoundary[1][0].x; col <= eyeBoundary[1][0].x + eyelashWidth; col++){
            pixels[eyeBoundary[1][0].y * width + col] = Color.rgb(255,0,255);
            pixels[(eyeBoundary[1][0].y - eyelashHeight) * width + col] = Color.rgb(255,0,255);
        }

        for(int row = (eyeBoundary[0][0].y - eyelashHeight); row <= eyeBoundary[0][0].y; row++){
            pixels[row * width + eyeBoundary[0][1].x - eyelashWidth] = Color.rgb(255,0,255);
            pixels[row * width + eyeBoundary[0][1].x] = Color.rgb(255,0,255);
            pixels[row * width + eyeBoundary[1][0].x] = Color.rgb(255,0,255);
            pixels[row * width + eyeBoundary[1][0].x + eyelashWidth] = Color.rgb(255,0,255);
        }

        //NOSE
        int noseHeight = (int)(0.31*(double)(face.faceBorder[1].y - face.faceBorder[0].y));
        noseBoundary[0] = new Point(eyeBoundary[0][1].x, eyeBoundary[0][1].y);
        noseBoundary[1] = new Point(eyeBoundary[1][0].x, eyeBoundary[0][1].y + noseHeight);
        for(int col = eyeBoundary[0][1].x; col <= eyeBoundary[1][0].x; col++){
            pixels[eyeBoundary[0][1].y * width + col] = Color.rgb(255,255,0);
            pixels[(eyeBoundary[0][1].y + noseHeight) * width + col] = Color.rgb(255,255,0);
        }

        for(int row = eyeBoundary[0][1].y; row <= eyeBoundary[0][1].y + noseHeight; row++){
            pixels[row * width + eyeBoundary[0][1].x] = Color.rgb(255,255,0);
            pixels[row * width + eyeBoundary[1][0].x] = Color.rgb(255,255,0);
        }
    }

    String analyzeFace(int[] outlinePixels, Face face, Point[] mouthBoundary, Point[][] eyeBoundary, Point[][] eyebrowBoundary, Point[] noseBoundary) {
        String result = "";
        int width = bitmap.getWidth();

        //Get Mouth Control Point
        //normalize mouth outline color
        int sumOfColorValue = 0;
        int countElmt = 0;
        int[][] temp = new int[mouthBoundary[1].y-mouthBoundary[0].y][mouthBoundary[1].x-mouthBoundary[0].x];
        for(int row = mouthBoundary[0].y; row < mouthBoundary[1].y; row++) {
            for(int col = mouthBoundary[0].x; col < mouthBoundary[1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
                temp[row-mouthBoundary[0].y][col-mouthBoundary[0].x] = Color.red(pixel);
            }
//            Log.d("MOUTH SOBEL MATRIX", Arrays.toString(temp[row-mouthBoundary[0].y]));
        }
        int blackWhiteThreshold = (int) (1.23 * (double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold 1", Integer.toString(blackWhiteThreshold));

        //get mouth left boundary
        Point mouthLeftBoundary = new Point(0,0);
        Point mouthRightBoundary = new Point(0, 0);
        int upperOffset = (int)(0.13 * (double)(mouthBoundary[1].y-mouthBoundary[0].y));
        int bottomOffset = (int)(0.06 * (double)(mouthBoundary[1].y-mouthBoundary[0].y));
        boolean found = false;
        for(int col = mouthBoundary[0].x; col <= mouthBoundary[1].x; col++) {
            for(int row = mouthBoundary[0].y + upperOffset; row <= mouthBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left mouth boundary found
                    mouthLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = mouthBoundary[1].x; col >= mouthBoundary[0].x; col--) {
            for(int row = mouthBoundary[0].y + upperOffset; row <= mouthBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //right mouth boundary found
                    mouthRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        mouthControlPoints[0] = mouthLeftBoundary;
        mouthControlPoints[8] = mouthRightBoundary;

        int stride = (mouthRightBoundary.x - mouthLeftBoundary.x) / 8;
        int stridePlus = (mouthRightBoundary.x - mouthLeftBoundary.x) % 8;
        if(stridePlus > 0) {
            stride++;
        }

        //set upper control point
        int idx = 1;
        found = false;
        for(int col = mouthLeftBoundary.x + stride; col < mouthRightBoundary.x; col += stride) {
            for(int row = mouthBoundary[0].y + upperOffset; row <= mouthBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    mouthControlPoints[idx] = new Point(col, row);
                    idx++;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        //set lower control point
        idx = 15;
        found = false;
        stride = (mouthRightBoundary.x - mouthLeftBoundary.x) / 8;
        stridePlus = (mouthRightBoundary.x - mouthLeftBoundary.x) % 8;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = mouthLeftBoundary.x + stride; col < mouthRightBoundary.x; col += stride) {
            for(int row = mouthBoundary[1].y - bottomOffset; row >= mouthBoundary[0].y + upperOffset; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    mouthControlPoints[idx] = new Point(col, row);
                    idx--;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        Log.d("Mouth control points", Arrays.toString(mouthControlPoints));

        /******************************************************************************************/
        //TODO Get eye control point
        //normalize left eye outline color
        sumOfColorValue = 0;
        countElmt = 0;
        for(int row = eyeBoundary[0][0].y; row < eyeBoundary[0][1].y; row++) {
            for(int col = eyeBoundary[0][0].x; col < eyeBoundary[0][1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
            }
        }
        blackWhiteThreshold = (int)((double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold 21", Integer.toString(blackWhiteThreshold));

        //get eye left boundary
        Point eyeLeftBoundary = new Point(0,0);
        Point eyeRightBoundary = new Point(0, 0);
        found = false;
        for(int col = eyeBoundary[0][0].x; col <= eyeBoundary[0][1].x; col++) {
            for(int row = eyeBoundary[0][0].y; row <= eyeBoundary[0][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = eyeBoundary[0][1].x; col >= eyeBoundary[0][0].x; col--) {
            for(int row = eyeBoundary[0][0].y; row <= eyeBoundary[0][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        eyesControlPoints[0][0] = eyeLeftBoundary;
        eyesControlPoints[0][5] = eyeRightBoundary;

        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 5;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 5;
        if(stridePlus > 0) {
            stride++;
        }

        //set upper control point
        idx = 1;
        found = false;
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyeBoundary[0][0].y; row <= eyeBoundary[0][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    eyesControlPoints[0][idx] = new Point(col, row);
                    idx++;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        //set lower control point
        idx = 9;
        found = false;
        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 5;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 5;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyeBoundary[0][1].y; row >= eyeBoundary[0][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    eyesControlPoints[0][idx] = new Point(col, row);
                    idx--;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        Log.d("Left Eye control points", Arrays.toString(eyesControlPoints[0]));

        /******************************************************************************************/
        //normalize right eye outline color
        sumOfColorValue = 0;
        countElmt = 0;
        for(int row = eyeBoundary[1][0].y; row < eyeBoundary[1][1].y; row++) {
            for(int col = eyeBoundary[1][0].x; col < eyeBoundary[1][1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
            }
        }
        blackWhiteThreshold = (int)((double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold 22", Integer.toString(blackWhiteThreshold));

        //get eye left boundary
        eyeLeftBoundary = new Point(0,0);
        eyeRightBoundary = new Point(0, 0);
        found = false;
        for(int col = eyeBoundary[1][0].x; col <= eyeBoundary[1][1].x; col++) {
            for(int row = eyeBoundary[1][0].y; row <= eyeBoundary[1][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = eyeBoundary[1][1].x; col >= eyeBoundary[1][0].x; col--) {
            for(int row = eyeBoundary[1][0].y; row <= eyeBoundary[1][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        eyesControlPoints[1][0] = eyeLeftBoundary;
        eyesControlPoints[1][5] = eyeRightBoundary;

        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 5;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 5;
        if(stridePlus > 0) {
            stride++;
        }

        //set upper control point
        idx = 1;
        found = false;
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyeBoundary[1][0].y; row <= eyeBoundary[1][1].y; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    eyesControlPoints[1][idx] = new Point(col, row);
                    idx++;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        //set lower control point
        idx = 9;
        found = false;
        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 5;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 5;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyeBoundary[1][1].y; row >= eyeBoundary[1][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    eyesControlPoints[1][idx] = new Point(col, row);
                    idx--;
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
        }

        Log.d("R Eye control points", Arrays.toString(eyesControlPoints[1]));

        /******************************************************************************************/
        //TODO EYEBROW CONTROL POINT
        //normalize left eyebrow outline color
        sumOfColorValue = 0;
        countElmt = 0;
        for(int row = eyebrowBoundary[0][0].y; row < eyebrowBoundary[0][1].y; row++) {
            for(int col = eyebrowBoundary[0][0].x; col < eyebrowBoundary[0][1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
            }
        }
        blackWhiteThreshold = (int)((double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold 31", Integer.toString(blackWhiteThreshold));

        //get eyebrow left boundary
        eyeLeftBoundary = new Point(0,0);
        eyeRightBoundary = new Point(0, 0);
        found = false;
        for(int col = eyebrowBoundary[0][0].x; col <= eyebrowBoundary[0][1].x; col++) {
            for(int row = eyebrowBoundary[0][1].y; row >= eyebrowBoundary[0][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = eyebrowBoundary[0][1].x; col >= eyebrowBoundary[0][0].x; col--) {
            for(int row = eyebrowBoundary[0][1].y; row >= eyebrowBoundary[0][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        eyebrowsControlPoints[0][0] = eyeLeftBoundary;
        eyebrowsControlPoints[0][6] = eyeRightBoundary;

        //set lower control point
        idx = 1;
        found = false;
        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 6;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 6;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyebrowBoundary[0][1].y; row >= eyebrowBoundary[0][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    eyebrowsControlPoints[0][idx] = new Point(col, row);
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
            if(eyebrowsControlPoints[0][idx] == null) {
                eyebrowsControlPoints[0][idx] = new Point(col, eyebrowsControlPoints[0][idx-1].y);
            }
            idx++;
        }

        Log.d("L EyeB control points", Arrays.toString(eyebrowsControlPoints[0]));

        //normalize right eyebrow outline color
        sumOfColorValue = 0;
        countElmt = 0;
        for(int row = eyebrowBoundary[1][0].y; row < eyebrowBoundary[1][1].y; row++) {
            for(int col = eyebrowBoundary[1][0].x; col < eyebrowBoundary[1][1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
            }
        }
        blackWhiteThreshold = (int)((double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold 31", Integer.toString(blackWhiteThreshold));

        //get eyebrow left boundary
        eyeLeftBoundary = new Point(0,0);
        eyeRightBoundary = new Point(0, 0);
        found = false;
        for(int col = eyebrowBoundary[1][0].x; col <= eyebrowBoundary[1][1].x; col++) {
            for(int row = eyebrowBoundary[1][1].y; row >= eyebrowBoundary[1][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = eyebrowBoundary[1][1].x; col >= eyebrowBoundary[1][0].x; col--) {
            for(int row = eyebrowBoundary[1][1].y; row >= eyebrowBoundary[1][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left eye boundary found
                    eyeRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        eyebrowsControlPoints[1][0] = eyeLeftBoundary;
        eyebrowsControlPoints[1][6] = eyeRightBoundary;

        //set lower control point
        idx = 1;
        found = false;
        stride = (eyeRightBoundary.x - eyeLeftBoundary.x) / 6;
        stridePlus = (eyeRightBoundary.x - eyeLeftBoundary.x) % 6;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = eyeLeftBoundary.x + stride; col < eyeRightBoundary.x; col += stride) {
            for(int row = eyebrowBoundary[1][1].y; row >= eyebrowBoundary[1][0].y; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold && idx < 7) { //upper mouth boundary found
                    eyebrowsControlPoints[1][idx] = new Point(col, row);
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
            }
            if(idx < 7 && eyebrowsControlPoints[1][idx] == null) {
                eyebrowsControlPoints[1][idx] = new Point(col, eyebrowsControlPoints[1][idx-1].y);
            }
            idx++;
        }
        Log.d("R EyeB control points", Arrays.toString(eyebrowsControlPoints[1]));

        /******************************************************************************************/
        //TODO NOSE CONTROL POINT
        //normalize right eyebrow outline color
        sumOfColorValue = 0;
        countElmt = 0;
        upperOffset = (noseBoundary[1].y-noseBoundary[0].y)/2 + (noseBoundary[1].y-noseBoundary[0].y)/10;
        bottomOffset = (noseBoundary[1].y-noseBoundary[0].y)/10;
        for(int row = noseBoundary[0].y + upperOffset; row < noseBoundary[1].y - bottomOffset; row++) {
            for(int col = noseBoundary[0].x; col < noseBoundary[1].x; col++) {
                int pixel = outlinePixels[row * width + col];
                countElmt++;
                sumOfColorValue += Color.red(pixel);
            }
        }
        blackWhiteThreshold = (int) (1.23 * (double)sumOfColorValue / (double)countElmt);
        Log.d("BlackWhite Threshold N", Integer.toString(blackWhiteThreshold));

        //get mouth left boundary
        Point noseLeftBoundary = new Point(0,0);
        Point noseRightBoundary = new Point(0, 0);

        found = false;
        for(int col = noseBoundary[0].x; col <= noseBoundary[1].x; col++) {
            for(int row = noseBoundary[0].y + upperOffset; row < noseBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //left mouth boundary found
                    noseLeftBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }

        found = false;
        for(int col = noseBoundary[1].x; col >= noseBoundary[0].x; col--) {
            for(int row = noseBoundary[0].y + upperOffset; row < noseBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //right mouth boundary found
                    noseRightBoundary = new Point(col, row);
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }
        noseControlPoints[0] = noseLeftBoundary;
        noseControlPoints[6] = noseRightBoundary;

        stride = (noseRightBoundary.x - noseLeftBoundary.x) / 6;
        stridePlus = (noseRightBoundary.x - noseLeftBoundary.x) % 6;
        if(stridePlus > 0) {
            stride++;
        }

        //set upper control point
        idx = 1;
        found = false;
        for(int col = noseLeftBoundary.x + stride; col < noseRightBoundary.x; col += stride) {
            for(int row = noseBoundary[0].y + upperOffset; row < noseBoundary[1].y - bottomOffset; row++) {
                int pixel = outlinePixels[row * width + col];
//                Log.d("idx", Integer.toString(idx));
                if(Color.red(pixel) > blackWhiteThreshold) { //upper nose boundary found
//                    Log.d("coord nose", Integer.toString(row) + " " + Integer.toString(col));
                    noseControlPoints[idx] = new Point(col, row);
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
                if(row == noseBoundary[1].y - bottomOffset - 1 &&
                        col <= noseLeftBoundary.x + 5 * stride/2 &&
                        col >= noseLeftBoundary.x + 7 * stride/2) {
                    noseControlPoints[idx] = new Point(col, noseLeftBoundary.y);
                    break;
                }
            }
            if(noseControlPoints[idx] == null) {
                noseControlPoints[idx] = new Point(col, noseControlPoints[idx-1].y);
            }
            idx++;
        }

        //set lower control point
        idx = 11;
        found = false;
        stride = (noseRightBoundary.x - noseLeftBoundary.x) / 6;
        stridePlus = (noseRightBoundary.x - noseLeftBoundary.x) % 6;
        if(stridePlus > 0) {
            stride++;
        }
        for(int col = noseLeftBoundary.x + stride; col < noseRightBoundary.x; col += stride) {
            for(int row = noseBoundary[1].y - bottomOffset - 1; row >= noseBoundary[0].y + upperOffset; row--) {
                int pixel = outlinePixels[row * width + col];
                if(Color.red(pixel) > blackWhiteThreshold) { //upper mouth boundary found
                    noseControlPoints[idx] = new Point(col, row);
                    if(stridePlus == 0 && !found) {
                        found = true;
                        stride--;
                    } else if (stridePlus > 0) {
                        stridePlus--;
                    }
                    break;
                }
                if(row == noseBoundary[0].y + upperOffset &&
                        col <= noseLeftBoundary.x + 5 * stride/2 &&
                        col >= noseLeftBoundary.x + 7 * stride/2) {
                    noseControlPoints[idx] = new Point(col, noseLeftBoundary.y);
                    break;
                }
            }
            if(noseControlPoints[idx] == null) {
                if(idx == 11) {
                    noseControlPoints[idx] = new Point(col, noseControlPoints[0].y);
                }else {
                    noseControlPoints[idx] = new Point(col, noseControlPoints[idx+1].y);
                }
            }
            idx--;
        }

        Log.d("Nose control points", Arrays.toString(noseControlPoints));

        /******************************************************************************************/

        //TODO Calculate Similarity according to gradient difference
        //Calculate gradients
        double[] mouthGradients = new double[16];
        double[][] eyeGradients = new double[2][10];
        double[][] eyebrowGradients = new double[2][6];
        double[] noseGradients = new double[12];
        for(int i = 0; i < mouthControlPoints.length; i++) {
            double gradient = 0.0;
            if (i != mouthControlPoints.length - 1) {
                gradient = (double)(mouthControlPoints[i + 1].y - mouthControlPoints[i].y) / (double)(mouthControlPoints[i + 1].x - mouthControlPoints[i].x);
            } else {
                gradient = (double)(mouthControlPoints[0].y - mouthControlPoints[i].y) / (double)(mouthControlPoints[0].x - mouthControlPoints[i].x);
            }
            mouthGradients[i] = gradient;
        }

        for(int pos = 0; pos < 2; pos++) {
            for(int i = 0; i < eyesControlPoints[pos].length; i++) {
                double gradient = 0.0;
                if (i != eyesControlPoints[pos].length - 1) {
                    gradient = (double)(eyesControlPoints[pos][i + 1].y - eyesControlPoints[pos][i].y) / (double)(eyesControlPoints[pos][i + 1].x - eyesControlPoints[pos][i].x);
                } else {
                    gradient = (double)(eyesControlPoints[pos][0].y - eyesControlPoints[pos][i].y) / (double)(eyesControlPoints[pos][0].x - eyesControlPoints[pos][i].x);
                }
                eyeGradients[pos][i] = gradient;
            }
        }

        for(int pos = 0; pos < 2; pos++) {
            for(int i = 0; i < eyebrowsControlPoints[pos].length - 1; i++) {
                double gradient = 0.0;
                if (i != eyebrowsControlPoints[pos].length - 1) {
                    gradient = (double)(eyebrowsControlPoints[pos][i + 1].y - eyebrowsControlPoints[pos][i].y) / (double)(eyebrowsControlPoints[pos][i + 1].x - eyebrowsControlPoints[pos][i].x);
                }
                eyebrowGradients[pos][i] = gradient;
            }
        }

        for(int i = 0; i < noseControlPoints.length; i++) {
            double gradient = 0.0;
            if (i != noseControlPoints.length - 1) {
                gradient = (double)(noseControlPoints[i + 1].y - noseControlPoints[i].y) / (double)(noseControlPoints[i + 1].x - noseControlPoints[i].x);
            } else {
                gradient = (double)(noseControlPoints[0].y - noseControlPoints[i].y) / (double)(noseControlPoints[0].x - noseControlPoints[i].x);
            }
            noseGradients[i] = gradient;
        }

        //Calculate distance
        int minFaceID = -1;
        double minDistance = Double.MAX_VALUE;
        for(int faceID = 0; faceID < labels.length; faceID++) {
            double mouthDistance = 0.0;
            double eyesDistance = 0.0;
            double eyebrowsDistance = 0.0;
            double noseDistance = 0.0;

            for(int i = 0; i < mouthGradients.length; i++) {
                mouthDistance += Math.abs(mouthGradients[i] - mouthTemplateGradients[faceID][i]);
            }
            for(int pos = 0; pos < 2; pos++) {
                for(int i = 0; i < eyeGradients[pos].length; i++) {
                    eyesDistance += Math.abs(eyeGradients[pos][i] - eyeTemplateGradients[faceID][pos][i]);
                }
            }
            for(int pos = 0; pos < 2; pos++) {
                for(int i = 0; i < eyebrowGradients[pos].length; i++) {
                    eyebrowsDistance += Math.abs(eyebrowGradients[pos][i] - eyebrowTemplateGradients[faceID][pos][i]);
                }
            }
            for(int i = 0; i < noseGradients.length; i++) {
                noseDistance += Math.abs(noseGradients[i] - noseTemplateGradients[faceID][i]);
            }


            double total = mouthDistance * mouthScoreWeight + eyesDistance * eyesScoreWeight + eyebrowsDistance * eyebrowsScoreWeight + noseDistance * noseScoreWeight;
            Log.d("difference", labels[faceID] + " " + Double.toString(total));
//            Log.d("nose diff", Double.toString(noseDistance));
            if(total < minDistance) {
                minDistance = total;
                minFaceID = faceID;
            }
        }
        Log.d("Face difference", Double.toString(minDistance));
        result = labels[minFaceID];
        return result;
    }

    void drawControlPoints(int[] pixels) {
        int width = bitmap.getWidth();

        //MOUTH
        for(int i = 0; i < mouthControlPoints.length; i++) {
            pixels[mouthControlPoints[i].y * width + mouthControlPoints[i].x] = Color.rgb(0,255,100);
        }

        //EYE
        for(int i = 0; i < eyesControlPoints.length; i++) {
            for(int j = 0; j < eyesControlPoints[i].length; j++) {
                pixels[eyesControlPoints[i][j].y * width + eyesControlPoints[i][j].x] = Color.rgb(0,255,100);
            }
        }

        //EYEBROW
        for(int i = 0; i < eyebrowsControlPoints.length; i++) {
            for(int j = 0; j < eyebrowsControlPoints[i].length; j++) {
                pixels[eyebrowsControlPoints[i][j].y * width + eyebrowsControlPoints[i][j].x] = Color.rgb(0,255,100);
            }
        }

        //NOSE
        for(int i = 0; i < noseControlPoints.length; i++) {
            pixels[noseControlPoints[i].y * width + noseControlPoints[i].x] = Color.rgb(0,255,100);
        }
    }

    void calculateAllGradients() {
        for(int faceID = 0; faceID < labels.length; faceID++) {
            //calculate mouth gradient
            for(int i = 0; i < mouthCPTemplate[faceID].length; i++) {
                double gradient = 0.0;
                if (i != mouthCPTemplate[faceID].length - 1) {
                    gradient = (double)(mouthCPTemplate[faceID][i + 1].y - mouthCPTemplate[faceID][i].y) / (double)(mouthCPTemplate[faceID][i + 1].x - mouthCPTemplate[faceID][i].x);
                } else {
                    gradient = (double)(mouthCPTemplate[faceID][0].y - mouthCPTemplate[faceID][i].y) / (double)(mouthCPTemplate[faceID][0].x - mouthCPTemplate[faceID][i].x);
                }
                mouthTemplateGradients[faceID][i] = gradient;
            }

            for(int pos = 0; pos < 2; pos++) {
                for(int i = 0; i < eyesCPTemplate[faceID][pos].length; i++) {
                    double gradient = 0.0;
                    if (i != eyesCPTemplate[faceID][pos].length - 1) {
                        gradient = (double)(eyesCPTemplate[faceID][pos][i + 1].y - eyesCPTemplate[faceID][pos][i].y) / (double)(eyesCPTemplate[faceID][pos][i + 1].x - eyesCPTemplate[faceID][pos][i].x);
                    } else {
                        gradient = (double)(eyesCPTemplate[faceID][pos][0].y - eyesCPTemplate[faceID][pos][i].y) / (double)(eyesCPTemplate[faceID][pos][0].x - eyesCPTemplate[faceID][pos][i].x);
                    }
                    eyeTemplateGradients[faceID][pos][i] = gradient;
                }
            }

            for(int pos = 0; pos < 2; pos++) {
                for(int i = 0; i < eyebrowsCPTemplate[faceID][pos].length - 1; i++) {
                    double gradient = 0.0;
                    if (i != eyebrowsCPTemplate[faceID][pos].length - 1) {
                        gradient = (double)(eyebrowsCPTemplate[faceID][pos][i + 1].y - eyebrowsCPTemplate[faceID][pos][i].y) / (double)(eyebrowsCPTemplate[faceID][pos][i + 1].x - eyebrowsCPTemplate[faceID][pos][i].x);
                    }
                    eyebrowTemplateGradients[faceID][pos][i] = gradient;
                }
            }

            for(int i = 0; i < noseCPTemplate[faceID].length; i++) {
                double gradient = 0.0;
                if (i != noseCPTemplate[faceID].length - 1) {
                    gradient = (double)(noseCPTemplate[faceID][i + 1].y - noseCPTemplate[faceID][i].y) / (double)(noseCPTemplate[faceID][i + 1].x - noseCPTemplate[faceID][i].x);
                } else {
                    gradient = (double)(noseCPTemplate[faceID][0].y - noseCPTemplate[faceID][i].y) / (double)(noseCPTemplate[faceID][0].x - noseCPTemplate[faceID][i].x);
                }
                noseTemplateGradients[faceID][i] = gradient;
            }
        }
    }
}
