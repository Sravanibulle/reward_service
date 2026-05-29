package com.reward.services.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.reward.services.model.Transaction;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldFetchTransactionsBetweenDates() {

        Transaction txn = buildTransaction(9001L, 101L, "Sravani Bulle", 120.0,
                LocalDate.now().minusDays(10));
        transactionRepository.save(txn);

        List<Transaction> result = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        101L,
                        LocalDate.now().minusMonths(1),
                        LocalDate.now());

        assertFalse(result.isEmpty());
        assertEquals(101L, result.get(0).getCustomerId());
    }

    @Test
    void shouldReturnEmptyWhenCustomerHasNoTransactionsInRange() {

        List<Transaction> result = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        9999L,
                        LocalDate.now().minusMonths(1),
                        LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotReturnTransactionsOutsideDateRange() {

        Transaction oldTxn = buildTransaction(9002L, 101L, "Sravani Bulle", 200.0,
                LocalDate.now().minusMonths(6));
        transactionRepository.save(oldTxn);

        List<Transaction> result = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(
                        101L,
                        LocalDate.now().minusMonths(3),
                        LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFetchAllTransactionsInDateRange() {

        Transaction t1 = buildTransaction(9003L, 101L, "Sravani Bulle", 120.0,
                LocalDate.now().minusDays(5));
        Transaction t2 = buildTransaction(9004L, 102L, "Sri Lakshmi", 180.0,
                LocalDate.now().minusDays(10));
        transactionRepository.save(t1);
        transactionRepository.save(t2);

        List<Transaction> result = transactionRepository
                .findByTransactionDateBetween(
                        LocalDate.now().minusMonths(1),
                        LocalDate.now());

        assertTrue(result.size() >= 2);
    }

    @Test
    void shouldReturnEmptyWhenNoTransactionsInRange() {

        List<Transaction> result = transactionRepository
                .findByTransactionDateBetween(
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(20));

        assertTrue(result.isEmpty());
    }

    private Transaction buildTransaction(Long id, Long customerId, String name,
                                          double amount, LocalDate date) {
        Transaction t = new Transaction();
        t.setTransactionId(id);
        t.setCustomerId(customerId);
        t.setCustomerName(name);
        t.setAmount(amount);
        t.setTransactionDate(date);
        return t;
    }
}
