package com.imagic.imagic;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LevensheinDistanceClassifier {
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

    // Properties
    int[][] skeletonMatrix;
    ArrayList<ArrayList<Point> > curveList;

    public LevensheinDistanceClassifier(int[][] skeletonMatrix) {
        curveList = new ArrayList<>();
        this.skeletonMatrix = skeletonMatrix;
    }

    private void extractFeature() {

    }

    private void extractCurve() {

    }

    private void extractStringFeature() {

    }
}
