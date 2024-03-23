package com.dcb.soliddcb.d.javatechie.bad;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingMall {

    private final DebitCard debitCard;

    public void doPurchaseSomething(long amount) {
        this.debitCard.doTransaction(amount);
    }

    public static void main(String[] args) {
        ShoppingMall shoppingMall = new ShoppingMall(new DebitCard());
        shoppingMall.doPurchaseSomething(5000);
        // if the payment fails due to some method, I'd give my credit card.
        // I'll have to change my implementation to purchase using CreditCard.
        // As you can see, doing that, will result code changes in a lot of places
        // which is quite cumbersome.
    }

}
