package com.dcb.soliddcb.d.dcb.good;

/**
 * So we will have this class but it will be taken care via interface implementation.
 */
public class AddOperation implements CalculatorOperation {

    @Override
    public int calculate(int a, int b) {
        return a + b;
    }

}
