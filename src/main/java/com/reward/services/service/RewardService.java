package com.reward.services.service;

import java.util.List;

import com.reward.services.model.RewardResponse;

public interface RewardService {

	RewardResponse getRewardsByCustomerId(Long customerId);

	List<RewardResponse> getAllCustomerRewards();
}
