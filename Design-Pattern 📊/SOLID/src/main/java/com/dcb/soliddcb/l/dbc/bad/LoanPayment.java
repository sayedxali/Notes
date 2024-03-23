package com.dcb.soliddcb.l.dbc.bad;

/**
 * This loan interface is responsible for payment related operations on loan account.
 * <p>
 * LoanPayment implemented by actual loans like Home Loan, Credit Card Loan etc.
 * <p>
 * For credit card/personal loan which is unsecured foreclosure and repayment is not allowed.
 * <p>
 * <a href="https://www.baeldung.com/java-liskov-substitution-principle#:~:text=The%20Liskov%20Substitution%20Principle,-4.1.&text=Barbara%20Liskov%2C%20defining%20it%20in,is%20a%20subtype%20of%20T">...</a>.
 */
public interface LoanPayment {

    void doPayment(int amount);

    void forceCloseLoan();

    void doRepayment(int amount);

}
