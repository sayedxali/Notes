package com.dcb.soliddcb.i.javatechie.bad;

public class PayTmImpl implements UPIPayments {

    @Override
    public void payMoney() {

    }

    @Override
    public void getScratchCard() {

    }

    @Override
    public void getCashBackAsCreditBalance() {
        throw new UnsupportedOperationException("not applicable.");
    }

}
