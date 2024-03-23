package com.dcb.soliddcb.d.dcb.bad;

/**
 * Parent module or main module of calculator which help to calculate
 * as per user's choice.
 * <p>
 * As per DIP rule it states :
 * <p>
 * High-level modules should not depend on low-level modules. Both should depend on abstractions.
 * <p>
 * --- So above rule is broken our calculator class is directly dependent on low level class.
 * Abstractions should not depend on details. Details should depend on abstractions
 * <p>
 * --- Also is dependent on actual class.
 */
public class Calculator {

    public int calculate(int a, int b, String operation) {
        int result = 0;
        switch (operation) {
            case "add":
                result = new AddOperation().add(a, b);
            case "sub":
                result = new SubOperation().sub(a, b);
        }
        return result;
    }

}