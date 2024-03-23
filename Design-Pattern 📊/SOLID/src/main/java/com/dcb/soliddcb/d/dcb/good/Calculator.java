package com.dcb.soliddcb.d.dcb.good;

/**
 * As per DIP rule it states :
 * <p>
 * High-level modules should not depend on modules. Both should depend on abstractions.
 * <p>
 * ——— So low level is dependent via `CalculatorOperation` rather being depended on add or subtract operation
 * Abstractions should not depend on details. Details should depend on abstractions.
 * <p>
 * ——— Abstraction is achieved as via interface we are entering in low level.
 */
public class Calculator {

    public int calculate(int a, int b, CalculatorOperation operation) {
        return operation.calculate(a, b);
    }

}
