package com.flaze.rewards.controller;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.exception.RewardsException;
import com.flaze.rewards.service.RewardsCalculatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.flaze.rewards.constants.RewardsConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RewardsCalculatorControllerTest {

    @MockBean
    private RewardsCalculatorService rewardsCalculatorServiceMock;

    @Autowired
    private MockMvc mockMvc;

    RewardsCalculatorController rewardsCalculatorController;
    RewardsDTO rewardsResponseMock;


    @Before
    public void setup() throws RewardsException {

        rewardsResponseMock = RewardsDTO.builder().customerId("123").transactionDate(LocalDate.now()).build();
        when(rewardsCalculatorServiceMock.getRewardsByMonth(anyString(), any(LocalDate.class))).thenReturn(rewardsResponseMock);
    }

    @Test
    public void getRewardsByDateTest() throws Exception {

        mockMvc
                .perform((MockMvcRequestBuilders.get(API + REWARDS_BY_DATE_PATH, "customerIdValue")
                        .param("date", "2022-08-12")
                        ))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void createTransactionTest() throws Exception{

        mockMvc
                .perform((MockMvcRequestBuilders.post(API + CREATE_TRANSACTION_PATH)
                        .queryParam("customer-id", "1234")
                        .queryParam("amount", "200.20")
                ))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}