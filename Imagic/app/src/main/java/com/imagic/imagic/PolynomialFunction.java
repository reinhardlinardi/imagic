package com.imagic.imagic;

/**
 * A class representing polynomial function.
 */
class PolynomialFunction {

    /* Properties */

    int degree; // Polynomial degree
    double[] coefficients; // Coefficients
    double constant; // Constant

    /* Methods */

    // Constructor
    PolynomialFunction(int degree, double constant) {
        this.degree = degree;
        this.constant = constant;

        coefficients = new double[degree];
    }

    // Compute function
    double compute(double x) {
        double total = 0;
        for(int power = degree; power > 0; power--) total += coefficients[degree - power] * Math.pow(x, power);

        return total + constant;
    }
}
