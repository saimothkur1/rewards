package com.flaze.rewards.service;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.exception.Error;
import com.flaze.rewards.exception.RewardsException;
import com.flaze.rewards.mapper.RewardsMapper;
import com.flaze.rewards.repository.RewardsRepository;
import com.flaze.rewards.schema.Rewards;
import com.flaze.rewards.schema.RewardsPK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.flaze.rewards.constants.RewardsConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardsCalculatorService {

    private final RewardsRepository rewardsRepository;


    public RewardsDTO createTransaction(String customerId, Double amount) throws RewardsException {

        log.debug("Entering createTransaction: {}, {}", customerId, amount);
        Rewards rewards = Rewards.builder()
                .rewardsPK(new RewardsPK(customerId, UUID.randomUUID().toString()))
                .amount(amount)
                .transactionDate(LocalDate.now())
                .points(calculatePoints(amount))
                .build();

        try {
           rewards  = rewardsRepository.save(rewards);
        } catch (Exception e) {
            throw new RewardsException(Collections.singletonList(new Error(DB_ERROR_INSERT_MESSAGE,
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getLocalizedMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return RewardsMapper.toRewardsDTO(rewardsRepository.save(rewards));
    }

    private Integer calculatePoints(Double amount) {

        //A customer receives 2 points for every dollar spent over $100 in each transaction,
        // plus 1 point for every dollar spent over $50 in each transaction

        double amountOver100 = 0;
        double amountOver50;
        int points = 0;

        if (amount > 100) {
            amountOver100 = amount - 100;
            amountOver50 = (amount - 50) - amountOver100;
            points = (int) (amountOver50 + (amountOver100 * 2));
        } else if (amount > 50) {
            amountOver50 = (amount - 50);
            points = (int) (amountOver50 + (amountOver100 * 2));
        }

        return points;
    }

    public RewardsDTO getRewardsByMonth(String customerId, LocalDate dateAfter) throws RewardsException {

        List<RewardsDTO> rewardsDTO;
        List<Rewards> dbRecords;

        dateAfter = Objects.isNull(dateAfter)? LocalDate.now().minusMonths(DEFAULT_MONTHS_TO_SUBTRACT)
                :dateAfter;

        try {
            dbRecords = rewardsRepository.findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(customerId, dateAfter);
        } catch (Exception e) {
            throw new RewardsException(Collections.singletonList(new Error(DB_ERROR_MESSAGE,
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getLocalizedMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (CollectionUtils.isEmpty(dbRecords)) {
            throw new RewardsException(Collections.singletonList(new Error(TRANSACTION_NOT_FOUND_ERROR_MESSAGE,
                    HttpStatus.NOT_FOUND.toString(), TRANSACTION_NOT_FOUND_ERROR_DETAIL + customerId)), HttpStatus.NOT_FOUND);
        }
        rewardsDTO = RewardsMapper.toDTOList(dbRecords);

        Map<YearMonth, Integer> totalPointsMap = rewardsDTO.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getTransactionDate()),
                        Collectors.collectingAndThen(Collectors.toList()
                                , a -> a.stream().mapToInt(RewardsDTO::getPoints).sum())));

        return RewardsDTO.builder().customerId(customerId).pointsByMonth(totalPointsMap).build();
    }

}

