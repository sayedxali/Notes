package com.dcb.soliddcb.s;

import com.dcb.soliddcb.s.bad.AccountOperations;
import com.dcb.soliddcb.s.good.TransactionOperations;

import java.math.BigDecimal;

public class Main {

    private static final AccountOperations accountOperations = new AccountOperations();
    private static final TransactionOperations transactionOperations = new TransactionOperations(accountOperations);


    public static void main(String[] args) {
        Account account = new Account();
        account.setAccountNumber(123);
        account.setFirstName("Vishrut");
        account.setTotalAmount(BigDecimal.valueOf(100000));

        accountOperations.addAccount(account);

        transactionOperations.deposit(BigDecimal.valueOf(123), 123);
    }

}
