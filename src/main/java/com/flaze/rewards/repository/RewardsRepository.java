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

    List<Rewards> findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(String customerId, LocalDate transactionDate);

}
