package com.imagic.imagic;

import java.util.ArrayList;

class ImageSkeleton {

    // Point
    private class Point{
        public int x;
        public int y;

        // Constructor
        public Point(int row, int col) {
            x = row;
            y = col;
        }
    }

    /*
    Zhang-Suen thinning algorithm neighbor numbering, p = current position
    // 7 0 1
    // 6 p 2
    // 5 4 3
     */

    // Constants
    private final int[][] neighbors = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
    private final int[][][] neighborGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    private final int WHITE = 0;
    private final int BLACK = 1;

    // Properties
    int[][] skeletonMatrix;

    // Constructor
    ImageSkeleton(int[][] blackWhiteImage) {
        skeletonMatrix = blackWhiteImage;
        getImageSkeleton();
    }

    // Count how many black neighbors
    private int countBlackNeighbors(int row, int col) {
        int count = 0;

        for (int i = 0; i < neighbors.length - 1; i++) {
            if(skeletonMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == 1) count++;
        }

        return count;
    }

    // Count how many white to black transition
    private int countWhiteToBlackTransition(int row, int col) {
        int count = 0;

        for(int i = 0; i < neighbors.length - 1; i++) {
            if(skeletonMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == WHITE) {
                if(skeletonMatrix[row + neighbors[i + 1][1]][col + neighbors[i + 1][0]] == BLACK) count++;
            }
        }

        return count;
    }

    // Is at least one is white
    private boolean isAtLeastOneIsWhite(int row, int col, int testPassNumber) {
        int count = 0;
        int[][] testGroup = neighborGroups[testPassNumber];

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < testGroup[i].length; j++) {
                int[] nbrTemp = neighbors[testGroup[i][j]];

                if(skeletonMatrix[row + nbrTemp[1]][col + nbrTemp[0]] == WHITE) {
                    count++;
                    break;
                }
            }
        }

        return count > 1;
    }

    // Turn image to skeleton using Zhang-Suen thinning algorithm
    private void getImageSkeleton() {
        ArrayList<Point> pixelsToBeWhitened = new ArrayList<>();
        boolean firstTest = false;
        boolean isChanged;

        do {
            isChanged = false;
            firstTest = !firstTest;

            for(int row = 1; row < skeletonMatrix.length - 1; row++) {
                for(int col = 1; col < skeletonMatrix[0].length - 1; col++) {
                    if(skeletonMatrix[row][col] != 1) continue;
                    int numberOfNeighbor = countBlackNeighbors(row, col);

                    if(numberOfNeighbor < 2 || numberOfNeighbor > 6) continue;
                    if(countWhiteToBlackTransition(row, col) != 1) continue;
                    if(!isAtLeastOneIsWhite(row, col, firstTest ? 0 : 1)) continue;

                    pixelsToBeWhitened.add(new Point(col, row));
                    isChanged = true;
                }
            }

            for(Point p : pixelsToBeWhitened) skeletonMatrix[p.y][p.x] = WHITE;
            pixelsToBeWhitened.clear();
        }
        while(firstTest || isChanged);
    }
}
