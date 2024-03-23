package com.dcb.soliddcb.o.good;

public class Main {

    public static void main(String[] args) {
        int calculatedNumber = new Calculator()
                .calculateNumber(1, 2, new AddOperation());
        System.out.println(calculatedNumber);
    }

}
