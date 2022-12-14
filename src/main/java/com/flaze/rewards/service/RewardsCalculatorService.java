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

    /**
     * This method allows users to create a transaction for given customerId and amount.
     * Transaction ID is autogenerated, Transaction date is defaulted to present Date,
     * and points are calculated using calculatePoints() Method.
     *
     * @param customerId: CustomerId for which transaction needs to be created.
     * @param amount: Amount of the transaction that needs to be created.
     * @return RewardsDTO: customerId, transactionId, transactionDate, Amount, Points.
     * @throws RewardsException: RewawrdsException is thrown when application has issues performing save operation
     * to persist into database
     */
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
        return RewardsMapper.toRewardsDTO(rewards);
    }

    /**
     * This private method is used to calculate points for a given amount.
     * 2 points are added for every dollar spent over $100 and 1 point is added for every dollar spent over $50.
     * @param amount: Amount for which points are to be calculated.
     * @return totalPoints: Calculated points for the given amount.
     */
    private Integer calculatePoints(Double amount) {

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

    /**
     * This method allows users to get rewards for each month starting from the dateAfter parameter passed.
     * DateAfter is defaulted to 3 months prior to today's date in case it is empty.
     * Utilizes findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual in the repository to fetch required records.
     * List of results are iterated and grouped using Collectors.groupingBy feature of the streams API to collect the required
     * yearMonth, total points for that month Map.
     * @param customerId: Customer ID for which the rewards need to be calculated.
     * @param dateAfter: Date after which the records need to be calculated. This parameter is Optional in the controller,
     *                 and this is defaulted to toady's date minus three months in case it is not passed by the controller.
     * @return RewardsDTO: customerId, pointsByMonth
     * @throws RewardsException:
     * 1. Rewards exception is thrown when no records are found for given customerId and dateAfter
     *    with 404 - NOT FOUND status code.
     * 2. Rewards exception is thrown with 500 Internal Server Error when application has issues connecting to database
     */
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

            String errorDetail = TRANSACTION_NOT_FOUND_ERROR_DETAIL + "CustomerId:" + customerId + ", dateAfter: " + dateAfter;

            throw new RewardsException(Collections.singletonList(new Error(TRANSACTION_NOT_FOUND_ERROR_MESSAGE,
                    HttpStatus.NOT_FOUND.toString(), errorDetail)), HttpStatus.NOT_FOUND);
        }
        rewardsDTO = RewardsMapper.toDTOList(dbRecords);

        Map<YearMonth, Integer> totalPointsMap = rewardsDTO.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getTransactionDate()),
                        Collectors.collectingAndThen(Collectors.toList()
                                , a -> a.stream().mapToInt(RewardsDTO::getPoints).sum())));

        return RewardsDTO.builder().customerId(customerId).pointsByMonth(totalPointsMap).build();
    }

}

