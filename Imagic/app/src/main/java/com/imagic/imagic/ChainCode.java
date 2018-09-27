package com.imagic.imagic;

class ChainCode {
    /*
    Chain code for all directions, p = current position

    7 0 1
    6 p 2
    5 4 3
    */

    // Chain codes
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
    private boolean[][] visited;

    // Edge detection chain code references for 0-9
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

    // Constructor
    ChainCode() { directionCodeCount = new int[8]; }

    // Scan line until encounter a black point as start point
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

            if(found) break;
        }

        return start;
    }

    // Get edge detection chain code
    void getEdgeDetectionChainCode(int[][] blackWhiteBitmap) {
        // Seek point initialization
        int[] startPoint = searchStartPoint(blackWhiteBitmap);
        int[] currentPoint;
        int[] previousPoint = new int[2];

        currentPoint = nextPoint(startPoint, previousPoint, blackWhiteBitmap);
        previousPoint[0] = startPoint[0];
        previousPoint[1] = startPoint[1];
        visited[currentPoint[1]][currentPoint[0]] = true;

        // Seek the bitmap
        int[] temp = new int[2];

        while(currentPoint[0] != startPoint[0] || currentPoint[1] != startPoint[1]) {
            temp[0] = currentPoint[0];
            temp[1] = currentPoint[1];
            currentPoint = nextPoint(currentPoint, previousPoint, blackWhiteBitmap);
            visited[currentPoint[1]][currentPoint[0]] = true;
            previousPoint[0] = temp[0];
            previousPoint[1] = temp[1];
        }
    }

    // Find next point
    private int[] nextPoint(int[] currentPoint, int[] previousPoint, int[][] blackWhiteBitmap) {
        // Array[y][x]
        int direction;

        if (previousPoint[0] == 0 && previousPoint[1] == 0) direction = CENTER;
        else direction = getCurrentDirection(previousPoint, currentPoint);

        int[] temp = new int[2];
        temp[0] = currentPoint[0];
        temp[1] = currentPoint[1] - 1;

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH) {
            directionCodeCount[NORTH]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1] - 1;

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH_WEST) {
            directionCodeCount[NORTH_EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1];

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != WEST) {
            directionCodeCount[EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0] + 1;
        temp[1] = currentPoint[1] + 1;

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH_WEST) {
            directionCodeCount[SOUTH_EAST]++;
            return temp;
        }

        temp[0] = currentPoint[0];
        temp[1] = currentPoint[1] + 1;

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH) {
            directionCodeCount[SOUTH]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1] + 1;

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != NORTH_EAST) {
            directionCodeCount[SOUTH_WEST]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1];

        if(isLegalPoint(blackWhiteBitmap, temp) && direction != EAST) {
            directionCodeCount[WEST]++;
            return temp;
        }

        temp[0] = currentPoint[0] - 1;
        temp[1] = currentPoint[1] - 1;

        if (isLegalPoint(blackWhiteBitmap, temp) && direction != SOUTH_WEST) {
            directionCodeCount[NORTH_WEST]++;
            return temp;
        }

        return temp;
    }

    // Get current direction
    public int getCurrentDirection(int[] previousPoint, int[] currentPoint) {
        int direction = CENTER;
        int dX = currentPoint[0] - previousPoint[0];
        int dY = currentPoint[1] - previousPoint[1];

        if (dY > 0 && dX == 0) direction = SOUTH;
        else if (dY > 0 && dX > 0) direction = SOUTH_EAST;
        else if (dY == 0 && dX > 0) direction = EAST;
        else if (dY < 0 && dX > 0) direction = NORTH_EAST;
        else if (dY < 0 && dX == 0) direction = NORTH;
        else if (dY < 0 && dX < 0) direction = NORTH_WEST;
        else if (dY == 0 && dX < 0) direction = WEST;
        else if (dY > 0 && dX < 0) direction = SOUTH_WEST;

        return direction;
    }

    // Check if current point is legal
    private boolean isLegalPoint(int[][] blackWhiteBitmap, int[] currentPoint) {
        return (!visited[currentPoint[1]][currentPoint[0]] &&
                blackWhiteBitmap[currentPoint[1]][currentPoint[0]] == 1 &&
                (blackWhiteBitmap[currentPoint[1] + 1][currentPoint[0]] == 0 ||
                blackWhiteBitmap[currentPoint[1]][currentPoint[0] + 1] == 0 ||
                blackWhiteBitmap[currentPoint[1] - 1][currentPoint[0]] == 0 ||
                blackWhiteBitmap[currentPoint[1]][currentPoint[0] - 1] == 0));
    }

    // Edge detection chain code OCR result
    int edgeDetectionOCR() {
        double[][] normalizedReference = new double[10][8];
        for(int i = 0; i < 10; i++) normalizedReference[i] = normalizeCodeChain(referenceCodeCount[i]);

        double[] normalizedTestChainCode = normalizeCodeChain(directionCodeCount);
        double[] sum = new double[10];
        double min = 999999.0;
        int result = -1;

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 8; j++) sum[i] += Math.abs(normalizedReference[i][j] - normalizedTestChainCode[j]);

            if(sum[i] < min) {
                min = sum[i];
                result = i;
            }
        }

        return result;
    }

    // Normalize code chain
    private double[] normalizeCodeChain(int[] chainCode) {
        int sum = 0;
        double[] result = new double[8];

        for(int i = 0; i < 8; i++) sum += chainCode[i];
        for(int i = 0; i < 8; i++) result[i] = (double) chainCode[i] / (double) sum;

        return result;
    }
}
