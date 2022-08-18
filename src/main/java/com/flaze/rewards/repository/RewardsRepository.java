package com.flaze.rewards.repository;

import com.flaze.rewards.schema.Rewards;
import com.flaze.rewards.schema.RewardsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, RewardsPK> {

    /**
     * This Derived Query is used to fetch all the rewards for each month starting from the dateAfter parameter passed
     * @param customerId: Customer ID for which the rewards need to be fetched.
     * @param transactionDate: Date after which the records need to be fetched.
     * @return List<Rewards>: List of Rewards objects.
     */
    List<Rewards> findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(String customerId, LocalDate transactionDate);

}
