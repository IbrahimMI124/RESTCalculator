
package com.example.calculator.exception;

import com.example.calculator.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST,
                "Parameter '" + ex.getName() + "' must be a valid number",
                request.getRequestURI());
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmetic(
            ArithmeticException ex,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST,
                "Invalid arithmetic operation",
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status, String message, String path) {

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity.status(status).body(body);
    }
}

//testing email
