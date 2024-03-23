package com.dcb.soliddcb.d.dcb.good;

/**
 * One more sub module for substation.
 */
public class SubOperation implements CalculatorOperation {

    @Override
    public int calculate(int a, int b) {
        return a - b;
    }

}
