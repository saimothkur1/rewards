package com.flaze.rewards.service;

import com.flaze.rewards.dto.RewardsDTO;
import com.flaze.rewards.exception.RewardsException;
import com.flaze.rewards.repository.RewardsRepository;
import com.flaze.rewards.schema.Rewards;
import com.flaze.rewards.schema.RewardsPK;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.flaze.rewards.constants.RewardsConstants.DEFAULT_MONTHS_TO_SUBTRACT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class RewardsCalculatorServiceTest {

    @Mock
    private RewardsRepository rewardsRepositoryMock;

    RewardsCalculatorService rewardsCalculatorService;

    Rewards rewardsMock;
    Rewards augustTransaction;
    Rewards julyTransaction;
    Rewards juneTransaction;
    Rewards secondJuneTransaction;

    List<Rewards> rewardsListMockData;

    @Captor
    ArgumentCaptor<Rewards> rewardsArgumentCaptor;

    @Captor
    ArgumentCaptor<LocalDate> dateCaptor;

    @Before
    public void setup() {

        rewardsMock = Rewards.builder().rewardsPK(new RewardsPK("1234", "123")).points(250).build();

        augustTransaction = new Rewards(new RewardsPK("12", "123"), LocalDate.of(2022,8,10),120.00,90);
        julyTransaction = new Rewards(new RewardsPK("12", "1234"), LocalDate.of(2022,7,10),100.00,50);
        juneTransaction = new Rewards(new RewardsPK("12", "312"), LocalDate.of(2022,6,10),60.0,10);
        secondJuneTransaction = new Rewards(new RewardsPK("12", "313"), LocalDate.of(2022,6,15),80.0,30);

        rewardsListMockData = new ArrayList<>();
        rewardsListMockData.add(augustTransaction);
        rewardsListMockData.add(julyTransaction);
        rewardsListMockData.add(juneTransaction);
        rewardsListMockData.add(secondJuneTransaction);

        rewardsCalculatorService = new RewardsCalculatorService(rewardsRepositoryMock);
        when(rewardsRepositoryMock.save(any(Rewards.class))).thenReturn(rewardsMock);
        when(rewardsRepositoryMock.findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), any(LocalDate.class)))
                .thenReturn(rewardsListMockData);
    }

    @Test
    public void createTransaction() throws RewardsException {
        RewardsDTO result = rewardsCalculatorService.createTransaction("1234", 120.0);

        verify(rewardsRepositoryMock, times(1)).save(rewardsArgumentCaptor.capture());

        assertEquals(rewardsArgumentCaptor.getAllValues().get(0).getRewardsPK().getCustomerId(), result.getCustomerId());
        assertEquals(rewardsMock.getRewardsPK().getTransactionId(), result.getTransactionId());
        assertEquals("90",String.valueOf(rewardsArgumentCaptor.getAllValues().get(0).getPoints()));
    }

    @Test(expected = RewardsException.class)
    public void createTransactionWithException() throws RewardsException {

        doThrow(new RuntimeException()).when(rewardsRepositoryMock).save(any(Rewards.class));

        rewardsCalculatorService.createTransaction("1234", 120.0);
        verify(rewardsRepositoryMock, times(1)).save(any(Rewards.class));

    }


    @Test
    public void getRewardsByMonthTest() throws RewardsException {
        RewardsDTO rewardsByMonth = rewardsCalculatorService.getRewardsByMonth("1234", LocalDate.of(2022, 8, 10));

        assertEquals(Integer.valueOf(40), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 6)));
        assertEquals(Integer.valueOf(50), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 7)));
        assertEquals(Integer.valueOf(90), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 8)));

        verify(rewardsRepositoryMock, times(1))
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), dateCaptor.capture());

        assertEquals(LocalDate.of(2022, 8, 10), dateCaptor.getValue());
    }

    @Test
    public void getRewardsByMonthTestWithDefaultMonths() throws RewardsException {
        RewardsDTO rewardsByMonth = rewardsCalculatorService.getRewardsByMonth("1234",null);

        assertEquals(Integer.valueOf(40), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 6)));
        assertEquals(Integer.valueOf(50), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 7)));
        assertEquals(Integer.valueOf(90), rewardsByMonth.getPointsByMonth().get(YearMonth.of(2022, 8)));

        verify(rewardsRepositoryMock, times(1))
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), dateCaptor.capture());

        assertEquals(LocalDate.now().minusMonths(DEFAULT_MONTHS_TO_SUBTRACT), dateCaptor.getValue());
    }

    @Test(expected = RewardsException.class)
    public void getRewardsByMonthTestWithNoTransactions() throws RewardsException {

        when(rewardsRepositoryMock.findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        rewardsCalculatorService.getRewardsByMonth("1234",null);
        verify(rewardsRepositoryMock, times(1))
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), dateCaptor.capture());
        assertEquals(LocalDate.now().minusMonths(DEFAULT_MONTHS_TO_SUBTRACT), dateCaptor.getValue());
    }

    @Test(expected = RewardsException.class)
    public void getRewardsByMonthTestWithDbException() throws RewardsException {

   doThrow(new RuntimeException()).when(rewardsRepositoryMock)
           .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), any(LocalDate.class));

        rewardsCalculatorService.getRewardsByMonth("1234",null);
        verify(rewardsRepositoryMock, times(1))
                .findAllByRewardsPK_CustomerIdAndTransactionDateIsGreaterThanEqual(anyString(), dateCaptor.capture());
        assertEquals(LocalDate.now().minusMonths(DEFAULT_MONTHS_TO_SUBTRACT), dateCaptor.getValue());
    }

}