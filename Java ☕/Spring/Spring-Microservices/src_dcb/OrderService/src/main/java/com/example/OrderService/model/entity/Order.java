package com.example.OrderService.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    private long quantity;
    private Instant orderDate;
    private String orderStatus;
    private long amount;

    /* relationships */
    private long productId;
    /* end of relationships */

}
