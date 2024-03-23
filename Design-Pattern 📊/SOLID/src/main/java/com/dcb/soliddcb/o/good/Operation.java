package com.dcb.soliddcb.o.good;

/**
 * This is good we keep adding new operation implementation
 * <p>
 * and our calculator get extensions without modifying it main task
 * <p>
 * that is performing calculation
 */
public interface Operation {

    int perform(int number1, int number2);

}
