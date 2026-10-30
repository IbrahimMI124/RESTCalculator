package com.example.calculator.dto;

public record CalculatorResponse(
        String operation,
        double a,
        double b,
        double result
) {
}
