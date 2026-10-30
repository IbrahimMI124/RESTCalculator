package com.example.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @Test
    void shouldAddTwoNumbers() {
        assertEquals(5.0, calculatorService.add(2.0, 3.0));
    }

    @Test
    void shouldSubtractTwoNumbers() {
        assertEquals(5.0, calculatorService.subtract(8.0, 3.0));
    }

    @Test
    void shouldMultiplyTwoNumbers() {
        assertEquals(12.0, calculatorService.multiply(4.0, 3.0));
    }

    @Test
    void shouldDivideTwoNumbers() {
        assertEquals(5.0, calculatorService.divide(10.0, 2.0));
    }

    @Test
    void shouldRejectDivisionByZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculatorService.divide(10.0, 0.0)
        );

        assertEquals(
                "Division by zero is not allowed",
                exception.getMessage()
        );
    }
}
