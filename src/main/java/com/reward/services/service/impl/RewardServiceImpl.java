package com.reward.services.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.reward.services.exception.CustomerNotFoundException;
import com.reward.services.model.MonthlyReward;
import com.reward.services.model.RewardResponse;
import com.reward.services.model.Transaction;
import com.reward.services.repository.TransactionRepository;
import com.reward.services.service.RewardCalculator;
import com.reward.services.service.RewardService;

@Service
public class RewardServiceImpl implements RewardService {

	private final TransactionRepository repository;
	private final RewardCalculator rewardCalculator;

	@Value("${rewards.last.months}")
	private int rewardMonths;

	public RewardServiceImpl(TransactionRepository repository, RewardCalculator rewardCalculator) {
		this.repository = repository;
		this.rewardCalculator = rewardCalculator;
	}

	@Override
	public RewardResponse getRewardsByCustomerId(Long customerId) {

		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusMonths(rewardMonths);

		List<Transaction> transactions =
				repository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

		if (transactions.isEmpty()) {
			throw new CustomerNotFoundException("Customer not found with id: " + customerId);
		}

		return buildRewardResponse(transactions);
	}

	@Override
	public List<RewardResponse> getAllCustomerRewards() {

		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusMonths(rewardMonths);

		List<Transaction> transactions = repository.findByTransactionDateBetween(startDate, endDate);

		Map<Long, List<Transaction>> customerTransactions = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getCustomerId));

		return customerTransactions.values().stream()
				.map(this::buildRewardResponse)
				.sorted(Comparator.comparing(RewardResponse::getCustomerId))
				.toList();
	}

	private RewardResponse buildRewardResponse(List<Transaction> transactions) {

		Map<Month, Integer> monthlyRewards = transactions.stream()
				.collect(Collectors.groupingBy(
						t -> t.getTransactionDate().getMonth(),
						Collectors.summingInt(t -> rewardCalculator.calculate(t.getAmount()))));

		List<MonthlyReward> monthlyRewardList = monthlyRewards.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(entry -> new MonthlyReward(entry.getKey().toString(), entry.getValue()))
				.toList();

		int totalPoints = monthlyRewardList.stream().mapToInt(MonthlyReward::getPoints).sum();

		Transaction first = transactions.get(0);
		return new RewardResponse(first.getCustomerId(), first.getCustomerName(), monthlyRewardList, totalPoints);
	}
}
