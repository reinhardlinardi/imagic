package com.imagic.imagic;

import java.util.ArrayList;
import java.util.List;

public class ImageSkeleton {
    public class Point{
        public int x;
        public int y;

        public Point(int row, int col) {
            x = row;
            y = col;
        }
    }

    private int[][] blackWhiteMatrix;

    // 7 0 1
    // 6 p 2
    // 5 4 3
    private final int[][] neighbors = {{0, -1}, {1, -1}, {1, 0},
            {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}};
    private final int[][][] neighborGroups = {{{0, 2, 4}, {2, 4, 6}},
            {{0, 2, 6}, {0, 4, 6}}};
    private List<Point> pixelsToBeWhitened;

    public ImageSkeleton(int[][] blackWhiteImage) {
        blackWhiteMatrix = blackWhiteImage;
        pixelsToBeWhitened = new ArrayList<>();
        skeletonizeImage();
    }

    private int countBlackNeighbors(int row, int col) {
        int count = 0;
        for (int i = 0; i < neighbors.length - 1; i++) {
            if (blackWhiteMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == 1) {
                count++;
            }
        }

        return count;
    }

    private int countWhiteToBlackTransition(int row, int col) {
        int count = 0;
        for (int i = 0; i < neighbors.length - 1; i++) {
            if (blackWhiteMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == 0) {
                if (blackWhiteMatrix[row + neighbors[i + 1][1]][col + neighbors[i + 1][0]] == 1) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean isAtLeastOneIsWhite(int row, int col, int testPassNumber) {
        int count = 0;
        int[][] testGroup = neighborGroups[testPassNumber];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < testGroup[i].length; j++) {
                int[] nbrTemp = neighbors[testGroup[i][j]];
                if (blackWhiteMatrix[row + nbrTemp[1]][col + nbrTemp[0]] == 0) {
                    count++;
                    break;
                }
            }
        }

        return count > 1;
    }

    private void skeletonizeImage() {
        // Zhang-Suen Algorithm
        boolean firstTest = false;
        boolean isChanged;
        do {
            isChanged = false;
            firstTest = !firstTest;
            for (int row = 1; row < blackWhiteMatrix.length - 1; row++) {
                for (int col = 1; col < blackWhiteMatrix[0].length - 1; col++) {
                    // Test
                    if (blackWhiteMatrix[row][col] != 1) {
                        continue;
                    }
                    int numberOfNeighbor = countBlackNeighbors(row, col);
                    if (numberOfNeighbor < 2 || numberOfNeighbor > 6) {
                        continue;
                    }
                    if (countWhiteToBlackTransition(row, col) != 1) {
                        continue;
                    }
                    if (!isAtLeastOneIsWhite(row, col, firstTest ? 0 : 1)) {
                        continue;
                    }

                    pixelsToBeWhitened.add(new Point(col, row));
                    isChanged = true;
                }
            }

            for (Point p : pixelsToBeWhitened) {
                blackWhiteMatrix[p.y][p.x] = 0;
            }
            pixelsToBeWhitened.clear();
        } while (firstTest || isChanged);
    }

    public int[][] getBlackWhiteMatrix() {
        return blackWhiteMatrix;
    }
}
