package com.flaze.rewards.service;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.mapper.RewardsMapper;
import com.flaze.rewards.repository.RewardsRepository;
import com.flaze.rewards.schema.Rewards;
import com.flaze.rewards.schema.RewardsPK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardsCalculatorService {

    private final RewardsRepository rewardsRepository;


    public RewardsDTO createTransaction(String customerId, Double amount) {

        Rewards rewards = Rewards.builder()
                .rewardsPK(new RewardsPK(customerId, UUID.randomUUID().toString()))
                .amount(amount)
                .transactionDate(LocalDate.now())
                .points(calculatePoints(amount))
                .build();

        return RewardsMapper.toRewardsDTO(rewardsRepository.save(rewards));
    }

    private Integer calculatePoints(Double amount) {

        //A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
        //(e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).

        double amountOver100 = 0;
        double amountOver50 = 0;
        int points = 0;

        if (amount > 100) {
            amountOver100 = amount - 100;
            amountOver50 = (amount - 50) - amountOver100;
            points = (int) (amountOver50 + (amountOver100 * 2));
        }

       else if (amount > 50) {
            amountOver50 = (amount - 50);
            points = (int) (amountOver50 + (amountOver100 * 2));
        }

        return points;
    }

    public RewardsDTO getRewardsByMonth(String customerId, LocalDate dateAfter){



        if(Objects.isNull(dateAfter)){
            dateAfter = LocalDate.now().minusMonths(3);
        }

        List<RewardsDTO> rewardsDTO = RewardsMapper
                .toDTOList(rewardsRepository.findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(customerId, dateAfter));

        Map<YearMonth, Integer> totalPointsMap = rewardsDTO.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getTransactionDate()),
                        Collectors.collectingAndThen(Collectors.toList()
                                , a -> a.stream().mapToInt(RewardsDTO::getPoints).sum())));

        LinkedHashMap<YearMonth, Integer> sorted = totalPointsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return  RewardsDTO.builder().customerId(customerId).pointsByMonth(sorted).build();
    }

}

