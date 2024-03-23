package com.dcb.soliddcb.d.dcb.good;

/**
 * This is level modules interface so anything
 * to invoke in modules needs to go via this interface
 * by this will achieve both loosely couple between high level and low level modeler
 * and abstraction as well.
 */
public interface CalculatorOperation {

    int calculate(int a, int b);

}
