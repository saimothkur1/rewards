package com.flaze.rewards.repository;


import com.flaze.rewards.schema.Rewards;
import com.flaze.rewards.schema.RewardsPK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RewardsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RewardsRepository rewardsRepository;


    @Test
    public void saveTest(){

        Rewards rewards = Rewards.builder()
                .points(100)
                .transactionDate(LocalDate.now())
                .rewardsPK(new RewardsPK("1234", "abc"))
                .amount(500.0)
                .build();
        Rewards savedReward = rewardsRepository.save(rewards);
        assertEquals(500.0,savedReward.getAmount());
    }


    @Test
    public void findRewardsFromTodayTest() {


        loadData();
        List<Rewards> dbRecords = rewardsRepository
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual("1234", LocalDate.now());

        //Should be one as loadData loads only one transaction for today
        assertEquals(1, dbRecords.size());
    }

    @Test
    public void findRewardsFromJune() {
        loadData();
        List<Rewards> dbRecords = rewardsRepository
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual("1234", LocalDate.of(2022, 6, 10));

        //Should bring back everything except one year old transaction
        assertEquals(3, dbRecords.size());
    }

    private void loadData(){
        rewardsRepository.deleteAll();
        Rewards rewardsTodayTransaction = Rewards.builder()
                .points(100)
                .transactionDate(LocalDate.now())
                .rewardsPK(new RewardsPK("1234", "abc"))
                .amount(500.0)
                .build();
        Rewards rewardsJuneTransaction = Rewards.builder()
                .points(100)
                .transactionDate(LocalDate.of(2022, 6, 10))
                .rewardsPK(new RewardsPK("1234", "abcd"))
                .amount(500.0)
                .build();

        Rewards rewardsJulyTransaction = Rewards.builder()
                .points(100)
                .transactionDate(LocalDate.of(2022, 7, 10))
                .rewardsPK(new RewardsPK("1234", "abcde"))
                .amount(500.0)
                .build();
        Rewards rewardsYearOldTransaction = Rewards.builder()
                .points(100)
                .transactionDate(LocalDate.of(2021, 1, 10))
                .rewardsPK(new RewardsPK("1234", "abcdef"))
                .amount(500.0)
                .build();
        List<Rewards> records = Arrays.asList(rewardsTodayTransaction, rewardsJuneTransaction, rewardsJulyTransaction, rewardsYearOldTransaction);

        rewardsRepository.saveAll(records);
    }

}