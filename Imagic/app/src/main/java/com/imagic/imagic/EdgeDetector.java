package com.imagic.imagic;

public class EdgeDetector {
    int MEDIAN = 0;
    int DIFFERENCE = 1;
    int HOMOGENOUS_DIFFERENCE = 2;
    int[][] imageBitmap;

    EdgeDetector(int[][] imageBitmap) {
        int width = imageBitmap[0].length;
        int height = imageBitmap.length;
        this.imageBitmap = new int[height + 2][width + 2];

        // pad image with 0 border
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                this.imageBitmap[row + 1][col + 1] = imageBitmap[row][col];
            }
        }
    }
    // Smooth image
    void smoothenImage() {

    }

    void sharpenImage() {

    }

    void convolute(int operatorCode) {
        
    }

    int computeNewColorValue(int[][] inputMatrix, int operatorCode) {
        int result = 0;

        return result;
    }
}
