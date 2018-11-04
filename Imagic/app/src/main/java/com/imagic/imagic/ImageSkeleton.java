package com.imagic.imagic;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

class ImageSkeleton {

    // Point
    private class Point{

        // Properties
        int row;
        int col;

        // Constructors
        Point(int row, int col) {
            this.col = col;
            this.row = row;
        }
    }

    // Constants
    private final int WHITE = 0;
    private final int BLACK = 1;

    private final int VERTEX_GREEN = 2;
    private final int INTERSECTION_BLUE = 3;

    private char[] charWithOneObject = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '9',
            '#', '$', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '<', '>', '[', ']', '\\', '^', '_', '`', '{', '|', '}', '~', '@',

            'A', 'A', 'A', 'a', 'B', 'B', 'B', 'B', 'b', 'b', 'b',
            'C', 'C', 'C', 'c', 'c', 'D', 'D', 'D', 'D', 'D', 'd', 'd',
            'E', 'E', 'e', 'e', 'F', 'F', 'F', 'f', 'f', 'f',
            'H', 'K', 'k', 'L', 'l', 'M', 'm', 'n', 'O', 'p', 'q', 'q', 'q',
            'T', 'T', 't', 'u', 'W', 'w', 'x', 'x', 'Y', 'y', 'y', 'y',
            'Z', 'z',
            'N', 'n', 'y', 'G', 'L', 'D', 'G', '&', 'q', '&',
            '4', '5', '>', ',', 'b', '*', 'r', '7', '6', 'u', '^', 'K', 'e',
            'R', '1', '2', '3', '5', '5', '6', '7', '7', '9', '9', '6', '/', '/', 'N', 'F', '4',
            '4', '6', ')', '(', ';', ')', '(', '>', '<', '~', '$', 'c', 'c', '[', ']', '[', '0',
            '^', '6', '6', 'z'
    };
    private int[][] referenceCodeCountOneObject = new int[][]{
            {21, 12, 39, 10, 42, 26, 29, 7}, // a
            {37, 9, 19, 13, 58, 15, 21, 8}, // b
            {6, 9, 30, 24, 28, 16, 8, 3}, // c
            {31, 12, 19, 9, 68, 11, 20, 11}, // d
            {14, 20, 37, 28, 27, 8, 44, 8}, // e noisy
            {3, 0, 20, 1, 62, 7, 10, 0}, // f
            {37, 13, 19, 9, 57, 18, 45, 19}, // g
            {0, 8, 20, 9, 106, 4, 1, 4}, // h
            {4, 23, 16, 24, 74, 12, 0, 10}, // k
            {0, 0, 0, 0, 68, 0, 0, 0}, // l
            {9, 10, 24, 13, 124, 13, 11, 1}, // m
            {9, 0, 9, 8, 79, 12, 10, 0}, // n
            {30, 12, 18, 12, 30, 12, 23, 12}, // o
            {43, 9, 19, 11, 54, 17, 23, 9}, // p
            {36, 12, 19, 9, 63, 10, 23, 13}, // q
            {0, 7, 11, 1, 44, 3, 0, 1}, // r
            {12, 0, 30, 23, 23, 17, 32, 6}, // s
            {2, 0, 16, 9, 60, 5, 7, 0}, // t
            {46, 0, 0, 1, 46, 11, 22, 4}, // u
            {39, 0, 0, 0, 30, 18, 8, 13}, // v
            {74, 1, 0, 0, 54, 34, 13, 20}, // w
            {26, 0, 4, 21, 26, 41, 15, 15}, // x
            {29, 18, 3, 18, 53, 12, 5, 4}, // y
            {1, 1, 72, 2, 14, 34, 1, 0}, // z noisy

            // Capital
            {35, 15, 5, 28, 56, 14, 37, 2}, // A
            {70, 0, 71, 19, 35, 17, 39, 4}, // B
            {8, 14, 47, 33, 42, 25, 10, 11}, // C
            {72, 0, 39, 16, 40, 17, 37, 3}, // D
            {2, 0, 137, 2, 65, 3, 0, 0}, // E
            {1, 0, 78, 1, 65, 3, 0, 0}, // F
            {26, 11, 54, 30, 42, 23, 35, 9}, // G
            {32, 1, 49, 1, 102, 1, 0, 0}, // H
            {0, 0, 0, 0, 67, 0, 0, 0}, // I
            {13, 0, 0, 0, 63, 8, 20, 4}, // J
            {51, 0, 4, 34, 54, 40, 17, 9}, // K,
            {0, 0, 37, 1, 68, 0, 0, 0}, // L
            {39, 26, 9, 28, 167, 6, 1, 6}, // M
            {22, 0, 0, 1, 124, 4, 9, 40}, // N
            {49, 18, 28, 18, 39, 26, 35, 18}, // O
            {39, 0, 38, 10, 47, 11, 38, 0}, // P
            {48, 18, 32, 28, 41, 25, 46, 25}, // Q
            {38, 0, 46, 36, 61, 13, 40, 14}, // R
            {17, 0, 44, 35, 31, 25, 39, 14}, // S
            {0, 1, 49, 1, 67, 0, 0, 0}, // T
            {57, 13, 25, 14, 56, 5, 1, 3}, // U
            {59, 0, 0, 0, 42, 29, 12, 18}, // V
            {117, 0, 0, 0, 94, 47, 8, 37}, // W
            {20, 26, 11, 52, 39, 33, 3, 6}, // X
            {28, 0, 2, 0, 47, 30, 11, 20}, // Y
            {18, 0, 90, 2, 36, 46, 0, 0}, // Z

            // Numbers
            {59, 13, 17, 14, 52, 17, 23, 15}, // 0
            {5, 0, 0, 1, 75, 18, 8, 0}, // 1
            {18, 0, 58, 12, 42, 58, 7, 3}, // 2
            {25, 0, 18, 20, 46, 39, 44, 8}, // 3
            {18, 29, 16, 2, 79, 12, 35, 1}, // 4
            {14, 4, 57, 13, 52, 23, 25, 9}, // 5
            {36, 11, 34, 27, 56, 35, 32, 10}, // 6
            {8, 1, 42, 1, 46, 31, 0, 0}, // 7
            //{52, 21, 38, 22, 40, 30, 33, 19}, // 8
            {40, 12, 19, 14, 52, 32, 44, 17}, // 9

            // Non alphabetic char
            {45, 9, 47, 3, 76, 21, 48, 0}, // # bad skeleton
            {78, 3, 26, 31, 50, 24, 35, 13}, // $
            {47, 24, 32, 23, 32, 42, 16, 25}, // &
            {1, 0, 0, 2, 11, 3, 0, 0}, // '
            {4, 0, 0, 16, 66, 16, 0, 6}, // (
            {5, 0, 0, 15, 68, 15, 1, 5}, // )
            {3, 5, 7, 7, 23, 8, 8, 6}, // *
            {1, 0, 20, 1, 40, 1, 21, 0}, // +
            {1, 0, 1, 2, 11, 4, 1, 0}, // ,
            {0, 0, 20, 0, 0, 0, 0, 0}, // -
            {0, 0, 0, 0, 0, 0, 0, 0}, // .
            {7, 0, 0, 0, 53, 21, 0, 0}, // /
            {5, 0, 23, 19, 6, 24, 21, 0}, // <
            {4, 0, 23, 18, 6, 24, 21, 0}, // >
            {0, 0, 23, 1, 90, 2, 0, 0}, // [
            {0, 0, 11, 1, 92, 0, 12, 0}, // ]
            {0, 0, 0, 20, 54, 0, 0, 3}, // \
            {2, 0, 3, 19, 33, 15, 2, 2}, // ^
            {0, 0, 56, 0, 0, 0, 0, 0}, // _
            {0, 0, 4, 8, 3, 0, 0, 2}, // `
            {5, 0, 7, 15, 65, 17, 5, 2}, // {
            {0, 0, 0, 0, 93, 0, 0, 0}, // |
            {3, 0, 5, 16, 64, 16, 7, 3}, // }
            {7, 0, 0, 0, 6, 16, 34, 4}, // ~
            {64, 34, 97, 56, 103, 76, 50, 24}, // @

            // handwritten
            {28, 23, 2, 19, 67, 31, 29, 7}, // A
            {30, 18, 2, 30, 73, 22, 31, 9}, // A
            {39, 9, 12, 57, 43, 16, 37, 18}, // A
            {11, 23, 13, 6, 39, 37, 5, 4}, // a
            {68, 21, 33, 13, 44, 48, 32, 9}, // B
            {79, 16, 30, 24, 47, 40, 37, 12}, // B
            {91, 10, 39, 28, 46, 39, 47, 13}, // B
            {90, 21, 60, 29, 46, 36, 39, 13}, // B
            {21, 8, 18, 8, 39, 22, 14, 2}, // b
            {18, 5, 20, 12, 38, 12, 17, 6}, // b
            {15, 6, 20, 7, 44, 11, 22, 5}, // b
            {10, 6, 40, 23, 41, 31, 11, 3}, // C
            {4, 4, 63, 27, 31, 25, 11, 2}, // C
            {3, 4, 45, 32, 50, 23, 8, 7}, // C
            {7, 11, 28, 12, 12, 30, 5, 2}, // c
            {8, 9, 28, 14, 21, 20, 8, 1}, // c
            {11, 0, 23, 21, 87, 48, 25, 3}, // D
            {67, 10, 24, 21, 30, 38, 36, 2}, // D
            {12, 0, 31, 24, 97, 40, 33, 7}, // D
            {75, 3, 32, 22, 35, 37, 42, 7}, // D
            {73, 4, 31, 22, 33, 29, 34, 6}, // D
            {14, 16, 23, 6, 60, 31, 25, 3}, // d
            {15, 18, 17, 10, 70, 29, 21, 4}, // d
            {5, 6, 79, 12, 55, 23, 35, 6}, // E
            {1, 6, 81, 16, 58, 10, 37, 3}, // E
            {16, 25, 29, 8, 13, 23, 8, 1}, // e
            {16, 23, 27, 16, 20, 22, 18, 4}, // e
            {4, 15, 52, 4, 49, 31, 19, 0}, // F
            {4, 7, 57, 14, 54, 15, 20, 2}, // F
            {3, 7, 58, 13, 52, 12, 20, 2}, // F
            {17, 5, 24, 11, 76, 25, 29, 5}, // f
            {7, 5, 24, 12, 75, 25, 13, 2}, // f
            {6, 6, 23, 17, 57, 21, 9, 2}, // f
            {30, 3, 0, 4, 104, 6, 50, 3}, // H
            {31, 0, 6, 23, 36, 35, 14, 11}, // K
            {2, 12, 19, 15, 51, 16, 3, 1}, // k
            {0, 9, 28, 3, 44, 4, 0, 0}, // L
            {10, 6, 13, 11, 37, 32, 3, 4}, // l
            {23, 0, 6, 5, 92, 31, 27, 10}, // M
            {8, 16, 19, 14, 42, 12, 0, 4}, // m
            {1, 14, 22, 9, 30, 9, 1, 4}, // n
            {39, 22, 23, 23, 37, 23, 32, 28}, // O
            {24, 12, 16, 14, 63, 17, 18, 9}, // p
            {16, 31, 31, 13, 53, 64, 14, 7}, // q
            {13, 17, 25, 5, 40, 31, 10, 3}, // q
            {11, 20, 18, 6, 61, 28, 14, 9}, // q
            {5, 0, 4, 1, 62, 23, 28, 1}, // T
            {1, 0, 2, 7, 40, 10, 41, 1}, // T
            {5, 10, 16, 8, 48, 14, 10, 0}, // t
            {22, 1, 0, 2, 26, 14, 10, 5}, // u
            {51, 0, 0, 0, 52, 27, 12, 23}, // W
            {43, 25, 18, 34, 43, 1, 0, 10}, // w
            {2, 24, 20, 15, 29, 20, 5, 2}, // x
            {1, 24, 22, 31, 15, 20, 8, 5}, // x
            {1, 27, 22, 28, 54, 11, 4, 9}, // Y
            {33, 15, 16, 8, 70, 16, 25, 17}, // y
            {37, 15, 17, 16, 63, 18, 38, 14}, // y
            {18, 19, 22, 18, 38, 37, 3, 4}, // y
            {16, 0, 45, 14, 25, 57, 52, 1}, // Z
            {11, 2, 55, 18, 25, 30, 10, 1}, // z
            {26, 0, 0, 7, 70, 4, 24, 16}, // N
            {4, 9, 12, 8, 45, 4, 0, 1}, // n
            {13, 0, 0, 0, 28, 22, 12, 11}, // y
            {17, 9, 23, 18, 24, 25, 25, 5}, // G
            {0, 1, 27, 5, 38, 0, 0, 0}, // L
            {40, 0, 12, 12, 24, 11, 10, 5}, // D
            {9, 7, 20, 13, 17, 17, 25, 3}, // G
            {32, 34, 19, 23, 23, 33, 17, 5}, // &
            {19, 22, 17, 7, 43, 19, 13, 2}, // q
            {21, 23, 12, 15, 20, 16, 17, 5}, // &
            {3, 10, 13, 4, 31, 5, 19, 2}, // 4
            {4, 0, 21, 15, 24, 8, 33, 4}, // 5
            {4, 0, 5, 15, 13, 26, 3, 2}, // >
            {0, 0, 0, 0, 6, 7, 1, 0}, // ,
            {18, 4, 10, 9, 40, 9, 12, 2}, // b
            {6, 6, 13, 5, 15, 13, 8, 8}, // *
            {6, 0, 12, 4, 18, 11, 2, 3}, // r
            {9, 0, 17, 1, 25, 28, 13, 1}, // 7
            {11, 1, 19, 9, 20, 25, 21, 12}, // 6
            {13, 0, 0, 0, 14, 6, 6, 6}, // u
            {1, 0, 5, 11, 10, 15, 2, 4}, // ^
            {2, 17, 34, 19, 42, 13, 9, 0}, // K
            {6, 8, 34, 11, 13, 11, 14, 1}, // e
            {28, 2, 31, 28, 22, 17, 13, 2}, // R
            {3, 1, 18, 6, 87, 26, 20, 0}, // 1
            {7, 0, 34, 16, 60, 34, 15, 3}, // 2
            {23, 12, 18, 20, 77, 61, 13, 4}, // 3
            {7, 0, 30, 17, 52, 31, 26, 5}, // 5
            {14, 1, 22, 15, 49, 53, 51, 6}, // 5
            {15, 17, 29, 7, 55, 45, 27, 7}, // 6
            {7, 0, 16, 1, 60, 28, 14, 0}, // 7
            {3, 0, 24, 5, 73, 17, 37, 4}, // 7
            {30, 8, 20, 14, 51, 31, 22, 15}, // 9
            {16, 18, 16, 5, 69, 36, 48, 8}, // 9
            {37, 8, 25, 6, 66, 32, 15, 12}, // 6
            {10, 0, 0, 0, 21, 25, 0, 0}, // /
            {5, 0, 0, 0, 10, 17, 0, 0}, // /
            {15, 0, 0, 4, 119, 11, 50, 27}, // N
            {2, 0, 42, 5, 36, 6, 21, 0}, // F
            {33, 5, 34, 9, 84, 4, 1, 3}, // 4
            {29, 2, 34, 3, 68, 24, 4, 4}, // 4
            {41, 6, 23, 13, 39, 40, 24, 14}, // 6
            {4, 0, 5, 16, 23, 17, 1, 4}, // )
            {4, 0, 2, 13, 29, 21, 2, 5}, // (
            {0, 0, 0, 0, 6, 4, 0, 0}, // ;
            {5, 0, 0, 10, 53, 19, 2, 5}, // )
            {10, 0, 0, 17, 52, 19, 0, 0}, // (
            {2, 0, 9, 15, 6, 19, 9, 3}, // >
            {1, 0, 6, 17, 3, 17, 6, 6}, // <
            {1, 5, 26, 17, 4, 8, 6, 5}, // ~
            {53, 7, 38, 23, 60, 15, 62, 9}, // $
            {6, 0, 34, 10, 9, 15, 6, 0}, // c
            {6, 5, 40, 12, 15, 19, 8, 2}, // c
            {0, 1, 44, 2, 75, 8, 19, 0}, // [
            {0, 0, 11, 1, 66, 7, 32, 0}, // ]
            {0, 2, 21, 8, 49, 3, 23, 0}, // [
            {23, 9, 9, 6, 21, 11, 11, 5}, // 0
            {2, 0, 4, 9, 13, 11, 0, 7}, // ^
            {32, 4, 36, 11, 39, 29, 27, 10}, // 6
            {251, 118, 160, 80, 417, 480, 190, 91}, // 6
            {5, 0, 56, 15, 27, 24, 1, 0}, // z
    };

    private char[] charWithOneObjectTwoCycle = {'B', '8', '@'};
    private int[][] referenceCodeCountOneObjectTwoCycle = new int[][] {
            {70, 0, 71, 19, 35, 17, 39, 4}, // B
            {52, 21, 38, 22, 40, 30, 33, 19}, // 8
            {37, 23, 36, 13, 31, 30, 46, 15}, // @
    };

    private char[] charWithTwoObjects = {'!', '"', ':', ';', '=', '?', 'i', 'j',
                                         ':', 'j', ';', 'j', 'i', 'j', '!'};
    private int[][] referenceCodeCountTwoObject = new int[][]{
            {0, 0, 0, 1, 48, 0, 0, 0}, // !
            {0, 0, 0, 4, 20, 8, 0, 0}, // "
            {0, 0, 0, 0, 0, 0, 0, 0}, // :
            {1, 0, 0, 4, 17, 7, 1, 0}, // ;
            {0, 0, 84, 0, 0, 0, 0, 0}, // =
            {4, 0, 10, 11, 27, 30, 8, 1}, // ?
//            0, 0, 0, 1, 7, 3, 0, 0
            {0, 0, 0, 0, 46, 0, 0, 0}, // i
            {2, 0, 0, 1, 65, 7, 5, 0}, // j

            {0, 0, 0, 0, 1, 0, 0, 0}, // :
            {13, 6, 4, 3, 61, 15, 17, 5}, // j
            {4, 0, 1, 0, 5, 9, 0, 0}, // ;
            {10, 0, 0, 3, 35, 6, 7, 3}, // j
            {0, 0, 1, 1, 17, 0, 0, 0}, // i
            {27, 16, 19, 7, 51, 23, 9, 4}, // j
            {0, 0, 0, 3, 24, 3, 0, 0}, // !

    };
    /*
    Zhang-Suen thinning algorithm neighbor numbering, p = current position
    // 7 0 1
    // 6 p 2
    // 5 4 3
     */
    private final int[][] neighbors = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
    private final int[][][] neighborGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};
    private int[] directionCodeCount;
    private int objectCount;
    private int cycleCount;

    // Properties
    int[][] skeletonMatrix;

    ArrayList<Point> vertex;
    ArrayList<Point> intersection;
    ArrayList<Pair<Point, Point> > mergedIntersection;
    ArrayList<Point> cycle;

    boolean[][] visited;

    int minRow = Integer.MAX_VALUE;
    int maxRow = -1;
    int minCol = Integer.MAX_VALUE;
    int maxCol = -1;

    // Constructor
    ImageSkeleton(int[][] blackWhiteImage) {
        skeletonMatrix = blackWhiteImage;
        directionCodeCount = new int[8];
        getImageSkeleton();
    }

    // Count how many black neighbors
    private int countBlackNeighbors(int row, int col) {
        int count = 0;

        for (int i = 0; i < neighbors.length - 1; i++) {
            if(skeletonMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == BLACK) count++;
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
                    if(skeletonMatrix[row][col] != BLACK) continue;
                    int numberOfNeighbor = countBlackNeighbors(row, col);

                    if(numberOfNeighbor < 2 || numberOfNeighbor > 6) continue;
                    if(countWhiteToBlackTransition(row, col) != 1) continue;
                    if(!isAtLeastOneIsWhite(row, col, firstTest ? 0 : 1)) continue;

                    pixelsToBeWhitened.add(new Point(row, col));
                    isChanged = true;
                }
            }

            for(Point p : pixelsToBeWhitened) skeletonMatrix[p.row][p.col] = WHITE;
            pixelsToBeWhitened.clear();
        }
        while(firstTest || isChanged);
    }

    // Get skeleton first point
    private Point getFirstPoint() {
        Point p = new Point(0,0);
        boolean found = false;

        int rows = skeletonMatrix.length;
        int cols = skeletonMatrix[0].length;

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                if(skeletonMatrix[row][col] == BLACK) {
                    p.col = col;
                    p.row = row;

                    found = true;
                    break;
                }
            }

            if(found) break;
        }

        return p;
    }

    // Reset visited
    private void resetVisited() {
        int rows = skeletonMatrix.length;
        int cols = skeletonMatrix[0].length;

        visited = new boolean[rows][cols];
        for(int row = 0; row < rows; row++) visited[row] = new boolean[cols];
    }

    // Inside border
    private boolean isInsideBorder(int row, int col) {
        int rows = skeletonMatrix.length;
        int cols = skeletonMatrix[0].length;

        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    // Check if point is vertex
    private boolean isVertex(int row, int col) {
        return countBlackNeighbors(row, col) <= 2 && countWhiteToBlackTransition(row, col) == 1;
    }

    // Check if point is intersection
    private boolean isIntersection(int row, int col) {
        return countBlackNeighbors(row, col) >= 3 && countWhiteToBlackTransition(row, col) >= 3;
    }

    // Extract all features (vertexes, intersections, and cycles) using DFS
    private void extractFeatures(int row, int col) {
        if(maxRow < row) maxRow = row;
        if(maxCol < col) maxCol = col;
        if(minRow > row) minRow = row;
        if(minCol > col) minCol = col;

        visited[row][col] = true;
//        Log.d("coord", Integer.toString(row) + " " + Integer.toString(col));
        if(isVertex(row, col)) {
            if(vertex.size() > 0) {
                int prevVertexIndex = vertex.size() - 1;
                if (row != vertex.get(prevVertexIndex).row || col != vertex.get(prevVertexIndex).col) {
                    vertex.add(new Point(row, col));
                }
            } else {
                vertex.add(new Point(row, col));
            }
            Log.d("COORD VERTEX", Integer.toString(row) + " " + Integer.toString(col));
        }
        else if(isIntersection(row, col)) {
            intersection.add(new Point(row, col));
        }

        // DFS
        for(int idx = 0; idx < neighbors.length; idx++) {
            int nextRow = row + neighbors[idx][1];
            int nextCol = col + neighbors[idx][0];

            if(isInsideBorder(nextRow, nextCol)) {
                if(skeletonMatrix[nextRow][nextCol] == BLACK && !visited[nextRow][nextCol]){
                    directionCodeCount[idx%8]++;
                    extractFeatures(nextRow, nextCol);
                }
            }
        }
    }

    void eliminateSkeletonNoise(Point intersectionPoint, ArrayList<Point> Points) {
        // Asumsi cuma ada 1 cabang noise setiap eliminasi
        int minimumDistance = Integer.MAX_VALUE;
        int maximumDistance = -1;
        int indexChosenPoint = -1;
        for(int i = 0; i < Points.size(); i++) {
            //TODO URGENT MODE - Manhattan distance NEED IMPROVEMENT (pengennya jarak absolut ngikutin pixelnya)
            int d = Math.abs(Points.get(i).row - intersectionPoint.row) + Math.abs(Points.get(i).col - intersectionPoint.col);
            Log.d("distances", Integer.toString(d));
            if (d < minimumDistance) {
                minimumDistance = d;
                indexChosenPoint = i;
            }
            if (d > maximumDistance) {
                maximumDistance = d;
            }
        }
//        hapus semua yang kurang dari threshold -> nanti ini mungkin bakal dipakai
//        for(int i = 0; i < Points.size(); i++) {
//            // URGENT MODE - Manhattan distance NEED IMPROVEMENT (pengennya jarak absolut ngikutin pixelnya)
//            int d = Math.abs(Points.get(i).row - intersectionPoint.row) + Math.abs(Points.get(i).col - intersectionPoint.col);
//            if ((double)d / (double)maximumDistance < 0.2) { // threshold kuli (indikator ujung palsu yg masih ccd)
//                deleteSkeletonEdge(intersectionPoint, Points.get(i));
//                vertex.remove(Points.get(i));
//            }
//        }

        Log.d("Distance ratio", Double.toString((double)minimumDistance / (double)maximumDistance));
        if ((double)minimumDistance / (double)maximumDistance < 0.22 &&
                minimumDistance != Integer.MAX_VALUE && maximumDistance != -1) {
            deleteSkeletonEdge(intersectionPoint, Points.get(indexChosenPoint));
            vertex.remove(Points.get(indexChosenPoint));
            intersection.remove(intersectionPoint);
        }
    }

    void deleteSkeletonEdge(Point start, Point end) {
        visited[start.row][start.col] = true;
        if (start.row != end.row || start.col != end.col) {
            for(int idx = 0; idx < neighbors.length; idx++) {
                int nextRow = start.row + neighbors[idx][1];
                int nextCol = start.col + neighbors[idx][0];

                if(isInsideBorder(nextRow, nextCol)) {
                    if(skeletonMatrix[nextRow][nextCol] == BLACK && !visited[nextRow][nextCol]) {
                        int d1 = Math.abs(end.row - start.row) + Math.abs(end.col - start.col);
                        int d2 = Math.abs(end.row - nextRow) + Math.abs(end.col - nextCol);
                        if(d2 < d1) {
                            Point next = new Point(nextRow, nextCol);
                            skeletonMatrix[nextRow][nextCol] = WHITE;
                            deleteSkeletonEdge(next, end);
                        }
                    }
                }
            }
        }
    }

    void mergeNeighborIntersection() {
        mergedIntersection = new ArrayList<>();
        if(intersection.size() >= 2) {
            for (int i = 0; i < intersection.size()-1; i++) {
                for(int j = i + 1; j < intersection.size(); j++) {
                    if (Math.abs(intersection.get(i).row - intersection.get(j).row) == 1 ||
                            Math.abs(intersection.get(i).col - intersection.get(j).col) == 1) {
                        mergedIntersection.add(new Pair<>(intersection.get(i), intersection.get(j)));
                        mergedIntersection.add(new Pair<>(intersection.get(j), intersection.get(i)));
                    }
                }
            }
        }
    }

    int getIntersectionCount() {
        return (intersection.size() - mergedIntersection.size()/2);
    }

    // Skeleton post-processing
    void postProcess() {
        resetVisited();

        vertex = new ArrayList<>();
        intersection = new ArrayList<>();
        cycle = new ArrayList<>();

//        Point start = getFirstPoint();
//        if(isVertex(start.row, start.col)) vertex.add(start);

        objectCount = 0;
        int rows = skeletonMatrix.length;
        int cols = skeletonMatrix[0].length;

        for(int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (skeletonMatrix[row][col] == BLACK && !visited[row][col]) {
                    Point point = new Point(row, col);
                    if(isVertex(point.row, point.col)) vertex.add(point);
                    objectCount++;
                    extractFeatures(row, col);
                }
            }
        }
//        extractFeatures(start.row, start.col);
        Log.d("Num of Objects", Integer.toString(objectCount));

        for(Point p : vertex) {
            Log.d("vertex:", Integer.toString(p.row) + " " + Integer.toString(p.col));
        }
        for(Point p : intersection) {
            Log.d("intersection:", Integer.toString(p.row) + " " + Integer.toString(p.col));
        }
        Log.d("vertex size: ", Integer.toString(vertex.size()));
        Log.d("intersecton size: ", Integer.toString(intersection.size()));

        // continue process skeleton
        int numOfIntersectNeighbor = 0;
        if (vertex.size() == 2 && intersection.size() == 1) {
            if (countWhiteToBlackTransition(intersection.get(0).row, intersection.get(0). col) <= 3) {
                intersection.clear();
            }
        } else if (vertex.size() == 3 && intersection.size() == 1) {
            numOfIntersectNeighbor = countWhiteToBlackTransition(intersection.get(0).row, intersection.get(0).col);
            if (numOfIntersectNeighbor == 3) {
                resetVisited();
                eliminateSkeletonNoise(intersection.get(0), vertex);
            }
        } else if (intersection.size() > 1) {
            for(int i = 0; i < intersection.size(); i++) {
                numOfIntersectNeighbor = countWhiteToBlackTransition(intersection.get(i).row, intersection.get(i).col);
                if(numOfIntersectNeighbor == 3) {
                    resetVisited();
                    eliminateSkeletonNoise(intersection.get(i), vertex); // vertex harus diganti jd koordinat kaki"
//                    KAKI
//                           /
//                    -----*/
//                kaki 2    \
//                           \kaki 1

                }
            }
        }
        Log.d("======================", "=================================");
        Log.d("vertex size: ", Integer.toString(vertex.size()));
        Log.d("intersecton size: ", Integer.toString(intersection.size()));
        for(Point p : vertex) {
            skeletonMatrix[p.row][p.col] = VERTEX_GREEN;
        }
        for(Point p : intersection) {
            skeletonMatrix[p.row][p.col] = INTERSECTION_BLUE;
        }

        //merge neighbor intersection
        mergeNeighborIntersection();

        //count cycle
        cycleCount = countCycle();
        Log.d("Cycle count", Integer.toString(cycleCount));
    }

    int countCycle() {
        if(vertex.size() == 0) {
            if(getIntersectionCount() == 0) return 1;
            else if(getIntersectionCount() == 1 || getIntersectionCount() == 2) return 2;
        } else if(getIntersectionCount() == 1 && (vertex.size() == 1 || vertex.size() == 2)) {
            return 1;
        }
        return 0;
    }

    char getPrediction() {
        char verdict = '\0';
        double[] normalizedTestCodeChain = normalizeCodeChain(directionCodeCount);

        Log.d("Chain code", Arrays.toString(directionCodeCount));
        if (objectCount == 3) {
            verdict = '%';
        } else if (objectCount == 0) {
            verdict = ' ';
        } else if (objectCount == 1 || objectCount == 2){
            verdict = searchTheMostSimilarChainCode(normalizedTestCodeChain);
        }
        return verdict;
    }

    private double[] normalizeCodeChain(int[] chainCode) {
        int sum = 0;
        double[] result = new double[8];

        for(int i = 0; i < 8; i++) sum += chainCode[i];
        for(int i = 0; i < 8; i++) {
            result[i] = (sum == 0) ? 0.0 : (double) chainCode[i] / (double) sum;
        }

        return result;
    }

    private char searchTheMostSimilarChainCode(double[] normalizedTestCodeChain) {
        char result = '\0';
        double minimumError = Double.MAX_VALUE;
        int resultIndex = 0;
        int[][] reference = new int[0][0];

        Log.d("cycle count predict", Integer.toString(cycleCount));
        if (objectCount == 1) {
            if (cycleCount == 2) {
                reference = referenceCodeCountOneObjectTwoCycle;
            } else {
                reference = referenceCodeCountOneObject;
            }
        } else if (objectCount == 2) {
            reference = referenceCodeCountTwoObject;
        }

        for(int i = 0; i < reference.length; i++) {
            double[] normalizedReference = normalizeCodeChain(reference[i]);
            double sum = 0;
            for(int j = 0; j < 8; j++) sum += Math.abs(normalizedReference[j] - normalizedTestCodeChain[j]);
            if (minimumError > sum) {
                minimumError = sum;
                resultIndex = i;
            }
            //Log.d("min sum", Double.toString(minimumError) + " " + Double.toString(sum));
        }

        //Log.d("result", Integer.toString(resultIndex));
        if (objectCount == 1) {
            if (cycleCount == 2) {
                result = charWithOneObjectTwoCycle[resultIndex];
            } else {
                result = charWithOneObject[resultIndex];
            }
        } else if (objectCount == 2) {
            result = charWithTwoObjects[resultIndex];
        }
        return result;
    }

    int getNumberPrediction(){
        int verdict = 999;

        if (countCycle() > 0){
            //Case 0,4,6,8,9
            if(countCycle() == 2){
                verdict = 8;
            } else {
                //Case 0,4,6,9
                if(vertex.size() == 0){
                    verdict = 0;
                } else {
                    //Case 4,6,9
                    if(vertex.size() == 2){
                        verdict = 4;
                    } else {
                        //Case 6,9
                        if(vertex.get(0).row < intersection.get(0).row){
                            verdict = 6;
                        } else {
                            verdict = 9;
                        }
                    }
                }
            }
        } else if (countCycle() == 0){
            //Case 1,2,3,5,7
            double[] normalizedTestCodeChain = normalizeCodeChain(directionCodeCount);
            Log.d("normalized code chain", Arrays.toString(normalizedTestCodeChain));
            if (vertex.size() == 3 && getIntersectionCount() == 1) {
                double dIntersectionTopRatio = (double)(intersection.get(0).row-minRow) / (double)(maxRow-minRow);
                if (normalizedTestCodeChain[4] < 0.48) {
                    if (dIntersectionTopRatio > 0.75) {
                        verdict = 2;
                    } else {
                        verdict = 3;
                    }
                } else {
                    verdict = 1;
                }
            } else {
                //Case 1,2,5,7
                double ratio = (double)(maxCol-minCol) / (double)(maxRow-minRow);
                if (ratio < 0.4) {
                    if (getIntersectionCount() >= 1) {
                        double dIntersectionTopRatio = (double)(intersection.get(0).row-minRow) / (double)(maxRow-minRow);
                        if (dIntersectionTopRatio <= 0.75) {
                            verdict = 3;
                        } else {
                            verdict = 2;
                        }
                    } else {
                        verdict = 1;
                    }
                } else { // 2, 5, 7
                    ratio = (double)Math.abs(vertex.get(0).col - vertex.get(1).col) / (double)(maxCol-minCol);
                    if (ratio < 0.6) {
                        verdict = 7;
                    } else {
                        if (isUpperVertexFront()) {
                            verdict = 2;
                        } else {
                            verdict = 5;
                        }
                    }
                }
            }
        }

        return verdict;
    }

    boolean isUpperVertexFront(){
        return ((vertex.get(0).col < vertex.get(1).col && vertex.get(0).row < vertex.get(1).row) ||
                (vertex.get(1).col < vertex.get(0).col && vertex.get(1).row < vertex.get(0).row) );
    }
}
