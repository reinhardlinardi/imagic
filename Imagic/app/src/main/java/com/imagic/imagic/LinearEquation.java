package com.imagic.imagic;

/**
 * A class representing linear equation.
 */
class LinearEquation {

    /* Properties */

    // Number of unknown variables
    int degree;

    // Coefficients and constants
    double[][] coefficients;
    double[] constants;

    /* Methods */

    // Constructor
    LinearEquation(int degree) {
        this.degree = degree;

        coefficients = new double[degree][];
        for(int row = 0; row < degree; row++) coefficients[row] = new double[degree];

        constants = new double[degree];
    }

    // Divide row
    private void divideRow(int row, double divisor) {
        for(int col = 0; col < degree; col++) coefficients[row][col] /= divisor;
        constants[row] /= divisor;
    }

    // Subtract row
    private void subtractRow(int row, int subtrahendRow, double multiplier) {
        for(int col = 0; col < degree; col++) coefficients[row][col] -= coefficients[subtrahendRow][col] * multiplier;
        constants[row] -= constants[subtrahendRow] * multiplier;
    }

    // Solve equation using Gauss-Jordan elimination (reduced row echelon form)
    double[] solve() {
        for(int col = 0; col < degree; col++) {
            divideRow(col, coefficients[col][col]);
            for(int row = col + 1; row < degree; row++) subtractRow(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        for(int col = degree - 1; col >= 1; col--) {
            for(int row = col - 1; row >= 0; row--) subtractRow(row, col, coefficients[row][col] / coefficients[col][col]);
        }

        double[] result = new double[degree];

        for(int row = 0; row < degree; row++) result[row] = Math.round(constants[row] * 1e8) / 1e8;
        return result;
    }

}
