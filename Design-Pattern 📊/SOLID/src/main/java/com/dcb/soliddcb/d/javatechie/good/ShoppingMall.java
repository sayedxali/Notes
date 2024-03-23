package com.dcb.soliddcb.d.javatechie.good;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingMall {

    private final BankCard bankCard;

    public void doPurchaseSomething(long amount) {
        this.bankCard.doTransaction(amount);
    }


    public static void main(String[] args) {
        BankCard bc = new DebitCard();
        ShoppingMall shoppingMall = new ShoppingMall(bc);
        shoppingMall.doPurchaseSomething(5000);
    }

}
