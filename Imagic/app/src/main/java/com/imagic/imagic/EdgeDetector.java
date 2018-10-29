package com.imagic.imagic;

import java.util.ArrayList;
import java.util.Collections;

public class EdgeDetector {
    int MEDIAN = 0;
    int DIFFERENCE = 1;
    int HOMOGENOUS_DIFFERENCE = 2;
    int[][] imageBitmap;
    int[][] convolutedImageBitmap;

    EdgeDetector(int[][] imageBitmap) {
        int width = imageBitmap[0].length;
        int height = imageBitmap.length;
        this.imageBitmap = new int[height + 2][width + 2];
        this.convolutedImageBitmap = new int[height][width];

        // pad image with 0 border
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                this.imageBitmap[row + 1][col + 1] = imageBitmap[row][col];
            }
        }
    }
    // Smooth image
    void smoothenImage() {
        convolute(MEDIAN);
    }

    void sharpenImageEdge(int operatorCode) {
        convolute(operatorCode);
    }

    void convolute(int operatorCode) {
        int width = imageBitmap[0].length;
        int height = imageBitmap.length;
        int[][] kernel = new int[3][3];

        for(int row = 1; row < height - 1; row++) {
            for(int col = 1; col < width - 1; col++) {
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        kernel[i][j] = imageBitmap[row - 1 + i][col - 1 + j];
                    }
                }

                convolutedImageBitmap[row - 1][col - 1] = computeNewColorValue(kernel, operatorCode);
            }
        }
    }

    int computeNewColorValue(int[][] kernel, int operatorCode) {
        int result = 0;
        if (operatorCode == MEDIAN) {
            ArrayList<Integer> valueList = new ArrayList<>();
            for(int i = 0; i < kernel.length; i++) {
                for(int j = 0; j < kernel[0].length; j++) {
                    valueList.add(kernel[i][j]);
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
            int j = kernel[0].length - 1;
            for(int i = 0; i < kernel[0].length; i++) {
                int diff = Math.abs(kernel[0][i] - kernel[kernel.length - 1][j]);
                if (diff > max) {
                    max = diff;
                }
                j--;
            }

            j = kernel.length - 1;
            for(int i = 0; i < kernel.length; i++) {
                int diff = Math.abs(kernel[i][0] - kernel[j][kernel[0].length - 1]);
                if (diff > max) {
                    max = diff;
                }
                j--;
            }

            result = max;
        } else if (operatorCode == HOMOGENOUS_DIFFERENCE) {

        }

        if (result < 0) {
            result = 0;
        } else if (result > 255) {
            result = 255;
        }

        return result;
    }
}
