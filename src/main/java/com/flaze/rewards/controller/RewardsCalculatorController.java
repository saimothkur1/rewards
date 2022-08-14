package com.flaze.rewards.controller;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.exception.RewardsException;
import com.flaze.rewards.service.RewardsCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

import static com.flaze.rewards.constants.RewardsConstants.*;

@RestController
@RequestMapping(API)
@RequiredArgsConstructor
@Validated
public class RewardsCalculatorController {

    private final RewardsCalculatorService rewardsCalculatorService;

    @PostMapping(CREATE_TRANSACTION_PATH)
    public ResponseEntity<RewardsDTO> createTransaction(@RequestParam(CUSTOMER_ID_PARAM) String customerId,
                                                        @RequestParam(AMOUNT_PARAM) @Positive Double amount) throws RewardsException {

        return new ResponseEntity<>(rewardsCalculatorService.createTransaction(customerId, amount), HttpStatus.CREATED);
    }

    @GetMapping(REWARDS_BY_DATE_PATH)
    public ResponseEntity<RewardsDTO> getRewardsByDate(@PathVariable(CUSTOMER_ID_PARAM) String customerId,
                                                       @RequestParam(value = DATE_AFTER_PARAM, required = false)
                                                       @DateTimeFormat(pattern = INPUT_DATE_FORMAT, iso = DateTimeFormat.ISO.DATE)
                                                       @PastOrPresent
                                                       LocalDate date) throws RewardsException {

        return ResponseEntity.ok(rewardsCalculatorService.getRewardsByMonth(customerId, date));
    }

}
