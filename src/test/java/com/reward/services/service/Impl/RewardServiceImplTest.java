package com.reward.services.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reward.services.exception.CustomerNotFoundException;
import com.reward.services.model.RewardResponse;
import com.reward.services.model.Transaction;
import com.reward.services.repository.TransactionRepository;
import com.reward.services.service.RewardCalculator;
import com.reward.services.service.impl.RewardServiceImpl;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RewardCalculator rewardCalculator;

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    void shouldThrowExceptionWhenNoTransactionsFound() {

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(999L), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class,
                () -> rewardService.getRewardsByCustomerId(999L));
    }

    @Test
    void shouldReturnRewardsWhenTransactionsExist() {

        Transaction txn = buildTransaction(101L, "Seshadri", 120.0, LocalDate.now());

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(101L), any(), any()))
                .thenReturn(List.of(txn));
        when(rewardCalculator.calculate(120.0)).thenReturn(90);

        RewardResponse response = rewardService.getRewardsByCustomerId(101L);

        assertNotNull(response);
        assertEquals(101L, response.getCustomerId());
        assertEquals("Seshadri", response.getCustomerName());
        assertEquals(90, response.getTotalPoints());
    }

    @Test
    void shouldCalculateMonthlyPointsAcrossMonths() {

        Transaction jan = buildTransaction(101L, "Seshadri", 120.0, LocalDate.of(2026, 1, 10));
        Transaction feb = buildTransaction(101L, "Seshadri", 80.0,  LocalDate.of(2026, 2, 10));

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(101L), any(), any()))
                .thenReturn(List.of(jan, feb));
        when(rewardCalculator.calculate(120.0)).thenReturn(90);
        when(rewardCalculator.calculate(80.0)).thenReturn(30);

        RewardResponse response = rewardService.getRewardsByCustomerId(101L);

        assertEquals(2, response.getMonthlyRewards().size());
        assertEquals(120, response.getTotalPoints());
    }

    @Test
    void shouldHandleBoundaryAmounts() {

        Transaction t50  = buildTransaction(101L, "Seshadri", 50.0,  LocalDate.now());
        Transaction t100 = buildTransaction(101L, "Seshadri", 100.0, LocalDate.now());

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(101L), any(), any()))
                .thenReturn(List.of(t50, t100));
        when(rewardCalculator.calculate(50.0)).thenReturn(0);
        when(rewardCalculator.calculate(100.0)).thenReturn(50);

        RewardResponse response = rewardService.getRewardsByCustomerId(101L);

        assertEquals(50, response.getTotalPoints());
    }

    @Test
    void shouldReturnZeroPointsWhenAllAmountsBelow50() {

        Transaction t1 = buildTransaction(101L, "Seshadri", 30.0, LocalDate.now());
        Transaction t2 = buildTransaction(101L, "Seshadri", 45.0, LocalDate.now());

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(101L), any(), any()))
                .thenReturn(List.of(t1, t2));
        when(rewardCalculator.calculate(30.0)).thenReturn(0);
        when(rewardCalculator.calculate(45.0)).thenReturn(0);

        RewardResponse response = rewardService.getRewardsByCustomerId(101L);

        assertEquals(0, response.getTotalPoints());
    }

    @Test
    void shouldHandleNegativeTransactionAmount() {

        Transaction txn = buildTransaction(101L, "Seshadri", -50.0, LocalDate.now());

        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(101L), any(), any()))
                .thenReturn(List.of(txn));
        when(rewardCalculator.calculate(-50.0)).thenReturn(0);

        RewardResponse response = rewardService.getRewardsByCustomerId(101L);

        assertEquals(0, response.getTotalPoints());
    }

    @Test
    void shouldReturnAllCustomerRewards() {

        Transaction t1 = buildTransaction(101L, "Seshadri", 120.0, LocalDate.now());
        Transaction t2 = buildTransaction(102L, "Raju",     180.0, LocalDate.now());

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(List.of(t1, t2));
        when(rewardCalculator.calculate(120.0)).thenReturn(90);
        when(rewardCalculator.calculate(180.0)).thenReturn(210);

        List<RewardResponse> responses = rewardService.getAllCustomerRewards();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsInWindow() {

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        List<RewardResponse> responses = rewardService.getAllCustomerRewards();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    private Transaction buildTransaction(Long customerId, String name, double amount, LocalDate date) {
        Transaction t = new Transaction();
        t.setCustomerId(customerId);
        t.setCustomerName(name);
        t.setAmount(amount);
        t.setTransactionDate(date);
        return t;
    }
}
