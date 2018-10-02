package com.imagic.imagic;

import android.util.Log;

import java.util.ArrayList;

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

    /*
    Zhang-Suen thinning algorithm neighbor numbering, p = current position
    // 7 0 1
    // 6 p 2
    // 5 4 3
     */
    private final int[][] neighbors = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
    private final int[][][] neighborGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    // Properties
    int[][] skeletonMatrix;

    ArrayList<Point> vertex;
    ArrayList<Point> intersection;
    ArrayList<Point> cycle;

    boolean[][] visited;

    // Constructor
    ImageSkeleton(int[][] blackWhiteImage) {
        skeletonMatrix = blackWhiteImage;
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
        visited[row][col] = true;
//        Log.d("coord", Integer.toString(row) + " " + Integer.toString(col));
        if(isVertex(row, col)) {
            if(vertex.size() > 0) {
                int prevVertexIndex = vertex.size() - 1;
                if (row != vertex.get(prevVertexIndex).row && col != vertex.get(prevVertexIndex).col) {
                    vertex.add(new Point(row, col));
                }
            } else {
                vertex.add(new Point(row, col));
            }
        }
        else if(isIntersection(row, col)) {
            intersection.add(new Point(row, col));
        }

        // DFS
        for(int idx = 0; idx < neighbors.length; idx++) {
            int nextRow = row + neighbors[idx][1];
            int nextCol = col + neighbors[idx][0];

            if(isInsideBorder(nextRow, nextCol)) {
                if(skeletonMatrix[nextRow][nextCol] == BLACK && !visited[nextRow][nextCol]) extractFeatures(nextRow, nextCol);
            }
        }
    }

    void eliminateSkeletonNoise(Point intersectionPoint, ArrayList<Point> Points) {
        // Asumsi cuma ada 1 cabang noise setiap eliminasi
        int minimumDistance = 9999999;
        int maximumDistance = -1;
        int indexChosenPoint = -1;
        for(int i = 0; i < Points.size(); i++) {
            // URGENT MODE - Manhattan distance NEED IMPROVEMENT (pengennya jarak absolut ngikutin pixelnya)
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

        if ((double)minimumDistance / (double)maximumDistance < 0.2) { // threshold kuli (indikator ujung palsu yg masih ccd)
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

    // Skeleton post-processing
    void postProcess() {
        resetVisited();

        vertex = new ArrayList<>();
        intersection = new ArrayList<>();
        cycle = new ArrayList<>();

        Point start = getFirstPoint();
        if(isVertex(start.row, start.col)) vertex.add(start);

        extractFeatures(start.row, start.col);
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
            intersection.clear();
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

        //count cycle
        //predict
    }
}
