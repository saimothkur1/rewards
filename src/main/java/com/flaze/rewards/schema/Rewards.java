package com.flaze.rewards.schema;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "REWARDS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rewards {

    @EmbeddedId
    private RewardsPK rewardsPK;

    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "POINTS")
    private Integer points;
}
