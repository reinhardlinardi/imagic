package com.imagic.imagic;

import java.util.Arrays;

public class ChainCode {
    // 0 1 2
    // 7 x 3
    // 6 5 4
    private final int NORTH = 0;
    private final int NORTH_EAST = 1;
    private final int EAST = 2;
    private final int SOUTH_EAST = 3;
    private final int SOUTH = 4;
    private final int SOUTH_WEST = 5;
    private final int WEST = 6;
    private final int NORTH_WEST = 7;
    private final int CENTER = 8;

    private int[] directionCodeCount;
    private int[][] referenceCodeCount = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    public ChainCode() {
        directionCodeCount = new int[8];
    }

    private int[] searchStartPoint(int[][] blackWhiteBitmap) {
        int[] start = new int[2]; // x, y
        int width = blackWhiteBitmap[0].length;
        int height = blackWhiteBitmap.length;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(blackWhiteBitmap[y][x] == 1) { // if black
                    start[0] = x;
                    start[1] = y;
                    break;
                }
            }
        }
        return start;
    }

    public void countDirectionCode(int[][] blackWhiteBitmap) {
        // seekPoint initialization
        int[] startPoint = searchStartPoint(blackWhiteBitmap);
        int[] currentPoint = new int[2];
        int[] previousPoint = new int[2];

        // seek the bitmap
        while(currentPoint[0] != startPoint[0] && currentPoint[1] != startPoint[1]) {

        }
    }

    public int[] nextPoint(int[] currentPoint, int[] previousPoint, int[][] blackWhiteBitmap) {
        // array[y][x]
        int[] next = new int[2];
        int width = blackWhiteBitmap[0].length;
        int height = blackWhiteBitmap.length;

        int direction = getCurrentDirection(previousPoint, currentPoint);

        int x, xplus, xmin, y, yplus, ymin;
        if (direction == NORTH) {

        } else if (direction == NORTH_EAST) {

        } else if (direction == EAST) {

        } else if (direction == SOUTH_EAST) {

        } else if (direction == SOUTH) {

        } else if (direction == SOUTH_WEST) {

        } else if (direction == WEST) {

        } else if (direction == NORTH_WEST) {

        }
//
//        if(blackWhiteBitmap[currentPoint[1] + 1][currentPoint[0]] == 1) { //N
//
//        } else if(blackWhiteBitmap[currentPoint[1] + 1][currentPoint[0] + 1] == 1) { //NE
//
//        } else if(blackWhiteBitmap[currentPoint[1]][currentPoint[0] + 1] == 1) { //E
//
//        } else if(blackWhiteBitmap[currentPoint[1] - 1][currentPoint[0] + 1] == 1) { //SE
//
//        } else if(blackWhiteBitmap[currentPoint[1] - 1][currentPoint[0]] == 1) { //S
//
//        } else if(blackWhiteBitmap[currentPoint[1] - 1][currentPoint[0] - 1] == 1) { //SW
//
//        } else if(blackWhiteBitmap[currentPoint[1]][currentPoint[0] - 1] == 1) { //W
//
//        } else if(blackWhiteBitmap[currentPoint[1] + 1][currentPoint[0] - 1] == 1) { //NW
//
//        }
        return next;
    }

    public int getCurrentDirection(int[] previousPoint, int[] currentPoint) {
        int direction = CENTER;
        int dX = currentPoint[0] - previousPoint[0];
        int dY = currentPoint[1] - previousPoint[1];

        if (dY > 0 && dX == 0) {
            direction = NORTH;
        } else if (dY > 0 && dX > 0) {
            direction = NORTH_EAST;
        } else if (dY == 0 && dX > 0) {
            direction = EAST;
        } else if (dY < 0 && dX > 0) {
            direction = SOUTH_EAST;
        } else if (dY < 0 && dX == 0) {
            direction = SOUTH;
        } else if (dY < 0 && dX < 0) {
            direction = SOUTH_WEST;
        } else if (dY == 0 && dX < 0) {
            direction = WEST;
        } else if (dY > 0 && dX < 0) {
            direction = NORTH_WEST;
        }

        return direction;
    }

    public int[] getLegalMove(int width, int height, int[] point) {
        int[] legalMove = new int[8];
        

        return legalMove;
    }
}
