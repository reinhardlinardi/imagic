package com.imagic.imagic;

import android.util.Log;

class LinearEquation {

    // Properties

    // Number of unknowns
    int degree;

    // Initial coefficients, right hand side, and result value
    double[][] coefficients;
    double[] rightHandSide;
    double[] result;

    // Constructor
    LinearEquation(int degree) {
        this.degree = degree;
        result = new double[degree];
    }

    // Set coefficients
    void setCoefficients(double[][] coefficients) { this.coefficients = coefficients; }

    // Set right hand side
    void setRightHandSide(double[] rightHandSide) { this.rightHandSide = rightHandSide; }

    // Subtract row
    private void rowSubtract(int targetRowIdx, int withRowIdx, double multiplier) {
        for(int col = 0; col < degree; col++) coefficients[targetRowIdx][col] -= coefficients[withRowIdx][col] * multiplier;
        rightHandSide[targetRowIdx] -= rightHandSide[withRowIdx] * multiplier;
    }

    // Divide entire row
    private void rowDivide(int targetRowIdx, double divideWith) {
        for(int col = 0; col < degree; col++) coefficients[targetRowIdx][col] /= divideWith;
        rightHandSide[targetRowIdx] /= divideWith;
    }

    // Solve equation using Gauss-Jordan elimination
    void solve() {
        for(int col = 0; col < degree - 1; col++) {
            rowDivide(col, coefficients[col][col]);
            for(int row = col + 1; row < degree; row++) rowSubtract(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        for(int col = degree - 1; col >= 1; col--) {
            for(int row = col - 1; row >= 0; row--) rowSubtract(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        for(int row = 0; row < degree; row++) result[row] = rightHandSide[row];
    }

    // Compute equation
    double compute(int x) {
        double total = 0;
        for(int power = 1; power <= degree; power++) total += result[power - 1] * Math.pow(x, power);

        return total;
    }
}
