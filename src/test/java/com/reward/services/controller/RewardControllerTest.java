package com.reward.services.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reward.services.model.MonthlyReward;
import com.reward.services.model.RewardResponse;
import com.reward.services.service.RewardService;

@ExtendWith(MockitoExtension.class)
class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    @Test
    void shouldReturnRewardsForValidCustomer() {

        Long customerId = 101L;
        RewardResponse mockResponse = new RewardResponse(
                customerId, "Sravani Bulle",
                List.of(new MonthlyReward("MARCH", 90)), 90);

        when(rewardService.getRewardsByCustomerId(customerId)).thenReturn(mockResponse);

        RewardResponse response = rewardController.getRewards(customerId);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals("Sravani Bulle", response.getCustomerName());
        assertEquals(90, response.getTotalPoints());
        assertFalse(response.getMonthlyRewards().isEmpty());
        verify(rewardService).getRewardsByCustomerId(customerId);
    }

    @Test
    void shouldDelegateToServiceForCustomerRewards() {

        Long customerId = 102L;
        when(rewardService.getRewardsByCustomerId(customerId))
                .thenReturn(new RewardResponse(customerId, "Sri Lakshmi", List.of(), 0));

        rewardController.getRewards(customerId);

        verify(rewardService, times(1)).getRewardsByCustomerId(customerId);
    }

    @Test
    void shouldReturnAllCustomerRewards() {

        List<RewardResponse> mockResponses = List.of(
                new RewardResponse(101L, "Sravani Bulle", List.of(new MonthlyReward("MARCH", 90)), 90),
                new RewardResponse(102L, "Sri Lakshmi", List.of(new MonthlyReward("MARCH", 210)), 210));

        when(rewardService.getAllCustomerRewards()).thenReturn(mockResponses);

        List<RewardResponse> response = rewardController.getAllCustomers();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Sravani Bulle", response.get(0).getCustomerName());
        verify(rewardService).getAllCustomerRewards();
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomers() {

        when(rewardService.getAllCustomerRewards()).thenReturn(List.of());

        List<RewardResponse> response = rewardController.getAllCustomers();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}
