package com.dcb.soliddcb.o.bad;

/**
 * This bad design where we are taking type
 * <p>
 * and for each type(calculating operation) we are having cases.
 * <p>
 * Now if we want to introduce division, we have to modify calculator; which violates open-close principle.
 */
public class Calculator {

    public int calculateNumber(int number1, int number2, String type) {
        int result = 0;
        switch (type) {
            case "sum":
                result = number1 + number2;
            case "sub":
                result = number1 - number2;
        }
        return result;
    }

}
