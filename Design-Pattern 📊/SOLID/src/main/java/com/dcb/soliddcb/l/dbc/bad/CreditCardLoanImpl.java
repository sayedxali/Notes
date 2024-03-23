package com.dcb.soliddcb.l.dbc.bad;

public class CreditCardLoanImpl implements LoanPayment {

    @Override
    public void doPayment(int amount) {

    }

    @Override
    public void forceCloseLoan() {
        throw new UnsupportedOperationException("Fore closure is not allowed.");
    }

    @Override
    public void doRepayment(int amount) {
        throw new UnsupportedOperationException("Repayment is not allowed.");
    }

}
