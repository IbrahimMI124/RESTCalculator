
package com.example.calculator.controller;

import com.example.calculator.dto.CalculatorResponse;
import com.example.calculator.service.CalculatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/add")
    public CalculatorResponse add(
            @RequestParam double a,
            @RequestParam double b) {
        return response("add", a, b, calculatorService.add(a, b));
    }

    @GetMapping("/subtract")
    public CalculatorResponse subtract(
            @RequestParam double a,
            @RequestParam double b) {
        return response(
                "subtract", a, b, calculatorService.subtract(a, b)
        );
    }

    @GetMapping("/multiply")
    public CalculatorResponse multiply(
            @RequestParam double a,
            @RequestParam double b) {
        return response(
                "multiply", a, b, calculatorService.multiply(a, b)
        );
    }

    @GetMapping("/divide")
    public CalculatorResponse divide(
            @RequestParam double a,
            @RequestParam double b) {
        return response("divide", a, b, calculatorService.divide(a, b));
    }

    private CalculatorResponse response(
            String operation, double a, double b, double result) {
        return new CalculatorResponse(operation, a, b, result);
    }
}
