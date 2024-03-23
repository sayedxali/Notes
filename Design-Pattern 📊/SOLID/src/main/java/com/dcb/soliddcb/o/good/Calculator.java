package com.dcb.soliddcb.o.good;

/**
 * No modifications in our calculator functionality
 * <p>
 * We can keep adding extension by create new operations
 */
public class Calculator {

    public int calculateNumber(int number1, int number2, Operation operation) {
        return operation.perform(number1, number2);
    }

}
