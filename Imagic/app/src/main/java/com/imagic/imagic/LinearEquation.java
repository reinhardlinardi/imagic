package com.imagic.imagic;

class LinearEquation {

    // Properties
    int numberOfVariables;
    Fraction[][] coefficient;
    Fraction[] result;

    // Constructor
    LinearEquation(int numberOfVariables, int[][] coefficient, int[] result) {
        this.numberOfVariables = numberOfVariables;
        this.coefficient = new Fraction[numberOfVariables][numberOfVariables];
        this.result = new Fraction[numberOfVariables];

        for(int row = 0; row < numberOfVariables; row++) {
            this.coefficient[row] = new Fraction[numberOfVariables];
            for(int col = 0; col < numberOfVariables; col++) this.coefficient[row][col] = new Fraction(coefficient[row][col]);
        }

        for(int row = 0; row < numberOfVariables; row++) this.result[row] = new Fraction(result[row]);
    }

    // Subtract row
    private void rowSubtract(int targetRowIdx, int withRowIdx, Fraction multiplier) {
        Fraction subtractValue;

        for(int col = 0; col < numberOfVariables; col++) {
            subtractValue = new Fraction(coefficient[withRowIdx][col], false);
            subtractValue.multiply(multiplier);
            coefficient[targetRowIdx][col].subtract(subtractValue);
        }

        subtractValue = new Fraction(result[withRowIdx], false);
        subtractValue.multiply(multiplier);
        result[targetRowIdx].subtract(subtractValue);
    }

    // Gauss-Jordan elimination
    double[] solve() {
        Fraction[] solutionInFraction = new Fraction[numberOfVariables];

        for(int col = 0; col < numberOfVariables - 1; col++) {
            for(int row = col + 1; row < numberOfVariables; row++) {
                Fraction multiplier = new Fraction(coefficient[row][col], false);
                multiplier.divide(coefficient[col][col]);
                rowSubtract(row, col, multiplier);
            }
        }

        for(int row = numberOfVariables - 1; row >= 0; row--) {
            Fraction total = new Fraction(0);

            for(int col = numberOfVariables - 1; col >= row; col--) {
                if(col == row) {
                    solutionInFraction[row] = new Fraction(result[row], false);
                    solutionInFraction[row].subtract(total);
                    solutionInFraction[row].divide(coefficient[row][col]);
                }
                else {
                    Fraction subTotal = new Fraction(coefficient[row][col], false);
                    subTotal.multiply(solutionInFraction[col]);
                    total.add(subTotal);
                }
            }
        }

        double[] solution = new double[numberOfVariables];
        for(int idx = 0; idx < numberOfVariables; idx++) solution[idx] = solutionInFraction[idx].toDouble();

        return solution;
    }
}
