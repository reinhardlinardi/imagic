package com.imagic.imagic;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LevenshteinDistanceClassifier {
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

    private final int[][] neighbors = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    // Properties
    int[][] skeletonMatrix;
    ArrayList<ArrayList<Point> > curveList;


    public LevenshteinDistanceClassifier(int[][] skeletonMatrix) {
        curveList = new ArrayList<>();
        this.skeletonMatrix = skeletonMatrix;
    }

    private void extractFeature() {
        extractCurve();
        extractStringFeature();
    }

    private void extractCurve() {
        // 4 3 2
        // 5 x 1
        // 6 7 8
        //TODO Chaincode segmentation
        int row = skeletonMatrix.length;
        int col = skeletonMatrix[0].length;

        boolean[][] visited= new boolean[row][col];
        for(int i=0;i<visited.length;i++){
            for(int j=0;j<visited[i].length;i++) {
                visited[i][j] = false;
            }
        }

        Point firstPoint = new Point(0,0);
        boolean found = false;
        //Assumption: Skeleton has been thinned

        //According to the paper, a curve is extracted by the following rules:
        //1. Found curve (trio code {1,2,3} {5,6,7} {3,4,5} {7,8,1}
        //2. Found branch (Intersection)
        //3. End of Component

        //Finding first point
        //Should get the top-left-most point
        for (int i=row-1;i>=0;i--){
            for(int j=0;j<row;j++){
                if (skeletonMatrix[i][j] == BLACK){
                    firstPoint.row = i;
                    firstPoint.col = j;
                    found = true;
                }
                if(found){break;}
            }
            if(found){break;}
        }

        //DFS
        //Nanti bisa taro fungsi DFS (iterateCurve) disini

    }

    private ArrayList<Point> iterateCurve(int row, int col,boolean[][] visited){
        //Assumption: First call of this function = first point, next call = recursion
        ArrayList<Point> curve = new ArrayList<Point>();

        visited[row][col] = true;

        for(int idx = 0; idx < neighbors.length; idx++) {
            int nextRow = row + neighbors[idx][1];
            int nextCol = col + neighbors[idx][0];

            if(skeletonMatrix[nextRow][nextCol] == BLACK && !visited[nextRow][nextCol]){
                iterateCurve(nextRow, nextCol,visited);
            }
        }


        return curve;
    }

    private boolean isCurve(int row, int col){
        //Paper Notation:
        //4 3 2
        //5   1
        //6 7 8
        boolean curve = false;
        if(countBlackNeighbors(row,col)==3){
            if((skeletonMatrix[row][col+1] == BLACK && skeletonMatrix[row-1][col+1] == BLACK && skeletonMatrix[row-1][col] == BLACK) ||
                (skeletonMatrix[row][col-1] == BLACK && skeletonMatrix[row+1][col-1] == BLACK && skeletonMatrix[row+1][col] == BLACK) ||
                (skeletonMatrix[row-1][col] == BLACK && skeletonMatrix[row-1][col-1] == BLACK && skeletonMatrix[row][col-1] == BLACK) ||
                (skeletonMatrix[row+1][col] == BLACK && skeletonMatrix[row+1][col+1] == BLACK && skeletonMatrix[row][col+1] == BLACK)){
                curve = true;
            }
        }
        return curve;
    }

    private boolean isVertex(int row, int col){
        return (countBlackNeighbors(row,col) <=1);
    }

    private boolean isIntersection(int row,int col){
        return(countBlackNeighbors(row,col)>=3);
    }

    private int countBlackNeighbors(int row, int col){
        int count = 0;
        for (int i = 0; i < neighbors.length - 1; i++) {
            if(skeletonMatrix[row + neighbors[i][1]][col + neighbors[i][0]] == BLACK) count++;
        }
        return count;
    }

    private void extractStringFeature() {
        //TODO String Feature Representation
    }

    private int calculateLevenshteinDistance(String template, String sample) {
        int result = 0;
        //TODO Levenshtein Distance Implementation
        return result;
    }

    public char predict() {
        char prediction = '\0';
        extractFeature();

        //TODO Loop to find minimum Levenshtein distance
        return prediction;
    }
}
