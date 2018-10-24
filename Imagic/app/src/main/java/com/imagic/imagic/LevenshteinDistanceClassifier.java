package com.imagic.imagic;

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
