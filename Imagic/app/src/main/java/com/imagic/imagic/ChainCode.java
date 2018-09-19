package com.imagic.imagic;

import android.util.Log;

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
            {94, 28, 39, 29, 92, 29, 39, 28},
            {135, 33, 20, 0, 148, 21, 31, 1},
            {60, 100, 122, 24, 70, 84, 144, 18},
            {88, 61, 106, 60, 86, 61, 108, 58},
            {80, 67, 34, 1, 146, 1, 100, 1},
            {111, 43, 153, 44, 105, 50, 145, 45},
            {97, 50, 76, 48, 93, 51, 78, 45},
            {94, 51, 95, 0, 90, 56, 89, 1},
            {66, 42, 54, 42, 68, 40, 56, 42},
            {93, 50, 77, 45, 98, 48, 76, 48},
    };
    private boolean[][] visited;

    public ChainCode() {
        directionCodeCount = new int[8];
    }

    private int[] searchStartPoint(int[][] blackWhiteBitmap) {
        int[] start = new int[2]; // x, y
        int width = blackWhiteBitmap[0].length;
        int height = blackWhiteBitmap.length;
        visited = new boolean[height][width];
        boolean found = false;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(blackWhiteBitmap[y][x] == 1) { // if black
                    start[0] = x;
                    start[1] = y;
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }
        return start;
    }

    public void countDirectionCode(int[][] blackWhiteBitmap) {
        // seekPoint initialization
        int[] startPoint = searchStartPoint(blackWhiteBitmap);
        int[] currentPoint = new int[2];
        int[] previousPoint = new int[2];

        currentPoint = nextPoint(startPoint, previousPoint, blackWhiteBitmap);
        previousPoint[0] = startPoint[0];
        previousPoint[1] = startPoint[1];
        visited[currentPoint[1]][currentPoint[0]] = true;
//        Log.v("curr point", Integer.toString(currentPoint[0]) + " " + Integer.toString(currentPoint[1]));
//        Log.v("startPoint", Integer.toString(startPoint[0]) + " " + Integer.toString(startPoint[1]));
        // seek the bitmap
        int[] temp = new int[2];
        while(currentPoint[0] != startPoint[0] || currentPoint[1] != startPoint[1]) {
            temp[0] = currentPoint[0];
            temp[1] = currentPoint[1];
            currentPoint = nextPoint(currentPoint, previousPoint, blackWhiteBitmap);
            visited[currentPoint[1]][currentPoint[0]] = true;
            previousPoint[0] = temp[0];
            previousPoint[1] = temp[1];
//            Log.v("curr point", Integer.toString(currentPoint[0]) + " " + Integer.toString(currentPoint[1]));
//            Log.v("startPoint", Integer.toString(startPoint[0]) + " " + Integer.toString(startPoint[1]));
        }

        Log.v("chain code", Arrays.toString(directionCodeCount));
    }

    public int[] nextPoint(int[] currentPoint, int[] previousPoint, int[][] blackWhiteBitmap) {
        // array[y][x]
        int direction;
        if (previousPoint[0] == 0 && previousPoint[1] == 0) {
            direction = CENTER;
        } else {
            direction = getCurrentDirection(previousPoint, currentPoint);
        }
//        Log.v("direction", Integer.toString(direction));
        int[] temp = new int[2];
        temp[0] = currentPoint[0];
        temp[1] = currentPoint[1] - 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH) {

//            Log.v("go to", "NORTH");
            directionCodeCount[NORTH]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1] - 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH_WEST) {
//            Log.v("go to", "NORTH EAST");
            directionCodeCount[NORTH_EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1];
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != WEST) {
//            Log.v("go to", "EAST");
            directionCodeCount[EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1] + 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH_WEST) {
//            Log.v("go to", "SOUTH EAST");
            directionCodeCount[SOUTH_EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0];
        temp[1] = currentPoint[1] + 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH) {
//            Log.v("go to", "SOUTH");
            directionCodeCount[SOUTH]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1] + 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH_EAST) {
//            Log.v("go to", "SOUTH WEST");
            directionCodeCount[SOUTH_WEST]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1];
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != EAST) {
//            Log.v("go to", "WEST");
            directionCodeCount[WEST]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1] - 1;
        if (isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH_WEST) {
//            Log.v("go to", "NORTH EAST");
            directionCodeCount[NORTH_WEST]++;
            return temp;
        }
        return temp;
    }

    public int getCurrentDirection(int[] previousPoint, int[] currentPoint) {
        int direction = CENTER;
        int dX = currentPoint[0] - previousPoint[0];
        int dY = currentPoint[1] - previousPoint[1];
//        Log.v("curr prev", Arrays.toString(currentPoint) + " " + Arrays.toString(previousPoint));
//        Log.v("dX dY", Integer.toString(dX) + " " + Integer.toString(dY));
        if (dY > 0 && dX == 0) {
            direction = SOUTH;
        } else if (dY > 0 && dX > 0) {
            direction = SOUTH_EAST;
        } else if (dY == 0 && dX > 0) {
            direction = EAST;
        } else if (dY < 0 && dX > 0) {
            direction = NORTH_EAST;
        } else if (dY < 0 && dX == 0) {
            direction = NORTH;
        } else if (dY < 0 && dX < 0) {
            direction = NORTH_WEST;
        } else if (dY == 0 && dX < 0) {
            direction = WEST;
        } else if (dY > 0 && dX < 0) {
            direction = SOUTH_WEST;
        }

        return direction;
    }

    public boolean isLegalPoint(int[][] blackWhiteBitmap, int[] currentPoint) {
        return (!visited[currentPoint[1]][currentPoint[0]] &&
                blackWhiteBitmap[currentPoint[1]][currentPoint[0]] == 1 &&
                (blackWhiteBitmap[currentPoint[1] + 1][currentPoint[0]] == 0 ||
                blackWhiteBitmap[currentPoint[1]][currentPoint[0] + 1] == 0 ||
                blackWhiteBitmap[currentPoint[1] - 1][currentPoint[0]] == 0 ||
                blackWhiteBitmap[currentPoint[1]][currentPoint[0] - 1] == 0));
    }

    public int predict() {
        double[][] normalizedReference = new double[10][8];
        for(int i = 0; i < 10; i++) {
            normalizedReference[i] = normalizeCodeChain(referenceCodeCount[i]);
        }
        double[] normalizedTestChainCode = normalizeCodeChain(directionCodeCount);

        double[] sum = new double[10];
        double min = 999999.0;
        int result = -1;
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 8; j++) {
                sum[i] += Math.abs(normalizedReference[i][j] - normalizedTestChainCode[j]);
            }
            if(sum[i] < min) {
                min = sum[i];
                result = i;
            }
        }

        return result;
    }

    public double[] normalizeCodeChain(int[] chainCode) {
        int sum = 0;
        for(int i = 0; i < 8; i++) {
            sum += chainCode[i];
        }

        double[] result = new double[8];
        for(int i = 0; i < 8; i++) {
            result[i] = (double) chainCode[i] / (double) sum;
        }
        return result;
    }
}
