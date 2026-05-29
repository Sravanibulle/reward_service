package com.reward.services.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reward.services.model.RewardResponse;
import com.reward.services.service.RewardService;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

	private final RewardService rewardService;

	public RewardController(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	@GetMapping("/{customerId}")
	public RewardResponse getRewards(@PathVariable Long customerId) {
		return rewardService.getRewardsByCustomerId(customerId);
	}

	@GetMapping("/allCustomers")
	public List<RewardResponse> getAllCustomers() {
		return rewardService.getAllCustomerRewards();
	}
}
