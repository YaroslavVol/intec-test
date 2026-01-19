package com.github.yaroslavvol.backend.service;

import com.github.yaroslavvol.backend.exception.IntervalOverlapException;
import com.github.yaroslavvol.backend.model.ActivityInterval;
import com.github.yaroslavvol.backend.repository.IntervalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntervalService {

    private final IntervalRepository intervalRepository;

    public List<ActivityInterval> getAllIntervals() {
        return intervalRepository.findAllByOrderByStartAsc();
    }

    @Transactional
    public ActivityInterval addInterval(ActivityInterval interval) {

        if (interval.getStart() >= interval.getEnd()) {
            throw new IllegalArgumentException("Start must be less than end");
        }

        List<ActivityInterval> overlapping = intervalRepository.findOverlappingIntervals(
                interval.getStart(),
                interval.getEnd()
        );

        if (!overlapping.isEmpty()) {
            ActivityInterval conflictInterval = overlapping.get(0);
            String errorMessage = String.format(
                    "Interval overlaps with existing interval: %s",
                    conflictInterval.toString()
            );
            throw new IntervalOverlapException(errorMessage);
        }

        return intervalRepository.save(interval);
    }

    public boolean hasOverlap(Integer start, Integer end) {
        List<ActivityInterval> overlapping = intervalRepository.findOverlappingIntervals(start, end);
        return !overlapping.isEmpty();
    }
}
