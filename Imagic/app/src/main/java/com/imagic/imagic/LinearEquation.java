package com.imagic.imagic;

class LinearEquation {

    // Properties

    // Number of unknowns
    int degree;

    // Initial coefficients, right hand side, and result value
    int[][] coefficients;
    int[] rightHandSide;
    double[] result;

    // Coefficients, right hand side, and result in fraction for solving equation
    private Fraction[][] fractionCoefficients;
    private Fraction[] fractionRightHandSide;
    private Fraction[] fractionResult;

    // Constructor
    LinearEquation(int degree) { this.degree = degree; }

    // Get variable coefficients in fraction
    private Fraction[][] getCoefficientsInFraction() {
        Fraction[][] coefficients = new Fraction[degree][degree];

        for(int row = 0; row < degree; row++) {
            coefficients[row] = new Fraction[degree];
            for(int col = 0; col < degree; col++) coefficients[row][col] = new Fraction(this.coefficients[row][col]);
        }

        return coefficients;
    }

    // Get right hand side in fraction
    private Fraction[] getRightHandSideInFraction() {
        Fraction[] rightHandSide = new Fraction[degree];
        for(int row = 0; row < degree; row++) rightHandSide[row] = new Fraction(this.rightHandSide[row]);

        return rightHandSide;
    }

    // Set coefficients
    void setCoefficients(int[][] coefficients) {
        this.coefficients = coefficients;
        this.fractionCoefficients = getCoefficientsInFraction();
    }

    // Set right hand side
    void setRightHandSide(int[] rightHandSide) {
        this.rightHandSide = rightHandSide;
        this.fractionRightHandSide = getRightHandSideInFraction();
    }

    // Subtract row
    private void rowSubtract(int targetRowIdx, int withRowIdx, Fraction multiplier) {
        Fraction subtractValue;

        for(int col = 0; col < degree; col++) {
            subtractValue = new Fraction(fractionCoefficients[withRowIdx][col]);
            subtractValue.multiply(multiplier);
            fractionCoefficients[targetRowIdx][col].subtract(subtractValue);
        }

        subtractValue = new Fraction(fractionRightHandSide[withRowIdx]);
        subtractValue.multiply(multiplier);
        fractionRightHandSide[targetRowIdx].subtract(subtractValue);
    }

    // Solve equation using Gauss-Jordan elimination
    void solve() {
        for(int col = 0; col < degree - 1; col++) {
            for(int row = col + 1; row < degree; row++) {
                Fraction multiplier = new Fraction(fractionCoefficients[row][col]);
                multiplier.divide(fractionCoefficients[col][col]);
                rowSubtract(row, col, multiplier);
            }
        }

        for(int row = degree - 1; row >= 0; row--) {
            Fraction total = new Fraction(0);

            for(int col = degree - 1; col >= row; col--) {
                if(col == row) {
                    fractionResult[row] = new Fraction(fractionRightHandSide[row]);
                    fractionResult[row].subtract(total);
                    fractionResult[row].divide(fractionCoefficients[row][col]);
                }
                else {
                    Fraction subTotal = new Fraction(fractionCoefficients[row][col]);
                    subTotal.multiply(fractionResult[col]);
                    total.add(subTotal);
                }
            }
        }

        for(int idx = 0; idx < degree; idx++) result[idx] = fractionResult[idx].toDouble();
    }

    // Compute equation
    double compute(int x) {
        double total = 0;
        for(int power = 1; power <= degree; power++) total += result[power - 1] * Math.pow(x, power);

        return total;
    }
}
