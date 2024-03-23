package com.dcb.soliddcb.s.good;

import com.dcb.soliddcb.s.Account;
import com.dcb.soliddcb.s.bad.AccountOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * This is good as it does transactions and at right place.
 */
@Service
@RequiredArgsConstructor
public class TransactionOperations {

    private final AccountOperations accountOperations;

    public void deposit(BigDecimal amount, int accountNumber) {
        //Getting account details it is job of account operations
        Account account = this.accountOperations.getAccount(accountNumber);
        account.setTotalAmount(account.getTotalAmount().add(amount));
    }


    public void withdraw(BigDecimal amount, int accountNumber) {
        Account account = this.accountOperations.getAccount(accountNumber);
        account.setTotalAmount(account.getTotalAmount().subtract(amount));
    }

}
