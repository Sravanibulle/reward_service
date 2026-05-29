package com.reward.services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RewardCalculatorTest {

    private final RewardCalculator calculator = new RewardCalculator();

    @Test
    void shouldReturnZeroForAmountBelow50() {
        assertEquals(0, calculator.calculate(25));
    }

    @Test
    void shouldReturnZeroForZeroAmount() {
        assertEquals(0, calculator.calculate(0));
    }

    @Test
    void shouldReturnZeroForNegativeAmount() {
        assertEquals(0, calculator.calculate(-10));
    }

    @Test
    void shouldReturnZeroForExactly50() {
        assertEquals(0, calculator.calculate(50));
    }

    @Test
    void shouldReturnOnePointFor51() {
        assertEquals(1, calculator.calculate(51));
    }

    @Test
    void shouldReturn25PointsFor75() {
        assertEquals(25, calculator.calculate(75));
    }

    @Test
    void shouldReturn49PointsFor99() {
        assertEquals(49, calculator.calculate(99));
    }

    @Test
    void shouldReturn50PointsForExactly100() {
        assertEquals(50, calculator.calculate(100));
    }

    @Test
    void shouldReturn52PointsFor101() {
        assertEquals(52, calculator.calculate(101));
    }

    @Test
    void shouldReturn90PointsFor120() {
        assertEquals(90, calculator.calculate(120));
    }

    @Test
    void shouldReturn250PointsFor200() {
        assertEquals(250, calculator.calculate(200));
    }

    @Test
    void shouldReturn350PointsFor250() {
        assertEquals(350, calculator.calculate(250));
    }
}
