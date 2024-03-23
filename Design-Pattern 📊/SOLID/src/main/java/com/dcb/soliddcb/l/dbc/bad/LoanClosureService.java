package com.dcb.soliddcb.l.dbc.bad;

import lombok.RequiredArgsConstructor;

/**
 * This is loan closure service which is responsible
 * to close the loan before its actual due date.
 * <p>
 * Now since credit card is not supporting foreclosure
 * it will throw exception which is wrong in design where
 * we are unable to substitute subtype with super type.That is violation of
 * Liskov Substitution rule.
 * <p>
 * Solution to this lets segregate the method in different super types
 * and make supertype substitute at any given time.
 */
@RequiredArgsConstructor
public class LoanClosureService {

    private final LoanPayment loanPayment;

    public void forceCloseLoan() {
        this.loanPayment.forceCloseLoan();;
    }

}