package com.flaze.rewards.controller;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.service.RewardsCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RewardsCalculatorController {

    /*TODO:
    *  BEAN VALIDATIONS
    * Exception handling
    * Test cases
    * REad me file
    *
    * *
    */


    private final RewardsCalculatorService rewardsCalculatorService;

    @PostMapping("/createtransaction")
    public ResponseEntity<RewardsDTO> createTransaction(@RequestParam("customer-id") String customerId,
                                                         @RequestParam("amount") Double amount){

        return  new ResponseEntity<>(rewardsCalculatorService.createTransaction(customerId, amount), HttpStatus.CREATED);
    }

    @GetMapping("/rewards/{customer-id}")
    public ResponseEntity<RewardsDTO> getRewardsByDate(@PathVariable("customer-id") String customerId,
                                                             @RequestParam(value = "date-after", required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
                                                             LocalDate date){

        return ResponseEntity.ok(rewardsCalculatorService.getRewardsByMonth(customerId, date));
    }

}
