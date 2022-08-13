package com.flaze.rewards.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RewardsPK implements Serializable {

    @Column(name = "CUSTOMER_ID", nullable = false)
    private String customerId;

    @Column(name = "TRANSACTION_ID", nullable = false)
    private String transactionId;
}
