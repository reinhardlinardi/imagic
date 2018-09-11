package com.imagic.imagic;

class Fraction {

    // Properties
    boolean negative;
    int numerator;
    int denominator;

    // Constructors
    Fraction(int number) {
        negative = (number < 0);
        numerator = Math.abs(number);
        denominator = 1;
    }

    Fraction(int numerator, int denominator) {
        this.negative = (numerator > 0 && denominator < 0 || numerator < 0 && denominator > 0);
        this.numerator = Math.abs(numerator);
        this.denominator = Math.abs(denominator);

        simplify();
    }

    Fraction(Fraction fraction, boolean complement) {
        negative = complement != fraction.negative;
        numerator = fraction.numerator;
        denominator = fraction.denominator;
    }

    // To double
    double toDouble() {
        double divisionResult = (double) numerator / denominator;
        return (negative)? -1 * divisionResult : divisionResult;
    }

    // GCD
    private static int getGCD(int a, int b) { return (b == 0)? a : getGCD(b, a % b); }

    // LCM
    private static int getLCM(int a, int b) { return a / getGCD(a, b) * b; }

    // Simplify fraction
    private void simplify() {
        int GCD = getGCD(numerator, denominator);
        numerator /= GCD;
        denominator /= GCD;
    }

    // Reverse fraction
    void reverse() {
        int temp = numerator;
        numerator = denominator;
        denominator = temp;
    }

    // Add
    void add(Fraction fraction) {
        int LCM = getLCM(denominator, fraction.denominator);

        int numeratorValue = (negative)? -1 * numerator : numerator;
        int fractionNumeratorValue = (fraction.negative)? -1 * fraction.numerator : fraction.numerator;
        int finalNumeratorValue = (numeratorValue * (LCM / denominator)) + (fractionNumeratorValue * (LCM / fraction.denominator));

        negative = (finalNumeratorValue < 0);
        numerator = Math.abs(finalNumeratorValue);
        denominator = LCM;

        simplify();
    }

    void add(int number) { add(new Fraction(number)); }

    // Subtract
    void subtract(Fraction fraction) { add(new Fraction(fraction, true)); }

    void subtract(int number) { subtract(new Fraction(number)); }

    // Multiply
    void multiply(Fraction fraction) {
        int numeratorValue = (negative)? -1 * numerator : numerator;
        int fractionNumeratorValue = (fraction.negative)? -1 * fraction.numerator : fraction.numerator;
        int finalNumeratorValue = numeratorValue * fractionNumeratorValue;

        negative = (finalNumeratorValue < 0);
        numerator = Math.abs(finalNumeratorValue);
        denominator *= fraction.denominator;

        simplify();
    }

    // Divide
    void divide(Fraction fraction) {
        Fraction reversedFraction = new Fraction(fraction, false);
        reversedFraction.reverse();
        multiply(reversedFraction);
    }

    void divide(int number) { divide(new Fraction(number)); }
}
