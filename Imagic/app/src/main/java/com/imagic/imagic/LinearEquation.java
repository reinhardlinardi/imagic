package com.imagic.imagic;

class LinearEquation {

    // Properties

    // Number of unknowns
    int degree;

    // Initial coefficients, right hand side, and result value
    double[][] coefficients;
    double[] rightHandSide;
    double[] result;
    double equationConstant;

    // Constructor
    LinearEquation(int degree, double constant) {
        this.degree = degree;
        result = new double[degree];
        this.equationConstant = constant;
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
        for(int col = 0; col < degree; col++) {
            rowDivide(col, coefficients[col][col]);
            for(int row = col + 1; row < degree; row++) rowSubtract(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        for(int col = degree - 1; col >= 1; col--) {
            for(int row = col - 1; row >= 0; row--) rowSubtract(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        for(int row = 0; row < degree; row++) result[row] = Math.round(rightHandSide[row] * 1e8) / 1e8;
    }

    // Compute equation
    double compute(int x) {
        double total = 0;
        for(int power = degree; power > 0; power--) total += result[degree - power] * Math.pow(x, power);

        return total + equationConstant;
    }
}
