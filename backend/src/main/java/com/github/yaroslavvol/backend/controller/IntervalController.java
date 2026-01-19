package com.github.yaroslavvol.backend.controller;

import com.github.yaroslavvol.backend.exception.IntervalOverlapException;
import com.github.yaroslavvol.backend.model.ActivityInterval;
import com.github.yaroslavvol.backend.service.IntervalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intervals")
@RequiredArgsConstructor
public class IntervalController {

    private final IntervalService intervalService;

    @GetMapping
    public ResponseEntity<List<ActivityInterval>> getAllIntervals() {
        List<ActivityInterval> intervals = intervalService.getAllIntervals();
        return ResponseEntity.ok(intervals);
    }

    @PostMapping
    public ResponseEntity<?> addInterval(@Valid @RequestBody ActivityInterval interval) {
        try {
            ActivityInterval savedIntervals = intervalService.addInterval(interval);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedIntervals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IntervalOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralError(Exception ex) {
        return createErrorResponse("Internal server error: " + ex.getMessage());
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
