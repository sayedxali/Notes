package com.dcb.soliddcb.s;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private int accountNumber;
    private String firstName;
    private BigDecimal totalAmount;

}
