package com.github.yaroslavvol.backend.service;

import com.github.yaroslavvol.backend.exception.IntervalOverlapException;
import com.github.yaroslavvol.backend.model.ActivityInterval;
import com.github.yaroslavvol.backend.model.ActivityType;
import com.github.yaroslavvol.backend.repository.IntervalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntervalServiceTest {

    @Mock
    private IntervalRepository intervalRepository;

    @InjectMocks
    private IntervalService intervalService;

    private ActivityInterval testInterval;

    @BeforeEach
    void setUp() {
        testInterval = new ActivityInterval(32400, 46800, ActivityType.WORK);
    }

    @Test
    void testAddInterval_Success() {

        when(intervalRepository.findOverlappingIntervals(anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        when(intervalRepository.save(any(ActivityInterval.class)))
                .thenReturn(testInterval);

        ActivityInterval result = intervalService.addInterval(testInterval);

        assertNotNull(result);
        assertEquals(32400, result.getStart());
        assertEquals(46800, result.getEnd());
        assertEquals(ActivityType.WORK, result.getType());
        verify(intervalRepository, times(1)).save(testInterval);
    }

    @Test
    void testAddInterval_InvalidStartEnd() {

        ActivityInterval invalidInterval = new ActivityInterval(50000, 40000, ActivityType.WORK);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> intervalService.addInterval(invalidInterval)
        );
        assertEquals("Start must be less than end", exception.getMessage());
        verify(intervalRepository, never()).save(any());
    }

    @Test
    void testAddInterval_OverlapDetected() {

        ActivityInterval existingInterval = new ActivityInterval(40000, 50000, ActivityType.BREAK);
        existingInterval.setId(1L);

        when(intervalRepository.findOverlappingIntervals(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(existingInterval));

        ActivityInterval newInterval = new ActivityInterval(45000, 55000, ActivityType.WORK);

        IntervalOverlapException exception = assertThrows(
                IntervalOverlapException.class,
                () -> intervalService.addInterval(newInterval)
        );
        assertTrue(exception.getMessage().contains("overlaps with existing interval"));
        verify(intervalRepository, never()).save(any());
    }

    @Test
    void testGetAllIntervals() {
        List<ActivityInterval> intervals = Arrays.asList(
                new ActivityInterval(32400, 46800, ActivityType.WORK),
                new ActivityInterval(46800, 50400, ActivityType.BREAK)
        );
        when(intervalRepository.findAllByOrderByStartAsc()).thenReturn(intervals);

        List<ActivityInterval> result = intervalService.getAllIntervals();

        assertEquals(2, result.size());
        verify(intervalRepository, times(1)).findAllByOrderByStartAsc();
    }

    @Test
    void testHasOverlap_True() {
        ActivityInterval existingInterval = new ActivityInterval(40000, 50000, ActivityType.WORK);
        when(intervalRepository.findOverlappingIntervals(45000, 55000))
                .thenReturn(Arrays.asList(existingInterval));

        boolean result = intervalService.hasOverlap(45000, 55000);

        assertTrue(result);
    }

    @Test
    void testHasOverlap_False() {
        when(intervalRepository.findOverlappingIntervals(60000, 70000))
                .thenReturn(new ArrayList<>());

        boolean result = intervalService.hasOverlap(60000, 70000);

        assertFalse(result);
    }

    @Test
    void testIntervalOverlap_EdgeCases() {
        // Test exact same interval
        ActivityInterval interval1 = new ActivityInterval(10000, 20000, ActivityType.WORK);
        ActivityInterval interval2 = new ActivityInterval(10000, 20000, ActivityType.BREAK);
        assertTrue(interval1.overlapsWith(interval2));

        // Test adjacent intervals (no overlap)
        ActivityInterval interval3 = new ActivityInterval(10000, 20000, ActivityType.WORK);
        ActivityInterval interval4 = new ActivityInterval(20000, 30000, ActivityType.BREAK);
        assertFalse(interval3.overlapsWith(interval4));

        // Test partial overlap
        ActivityInterval interval5 = new ActivityInterval(10000, 20000, ActivityType.WORK);
        ActivityInterval interval6 = new ActivityInterval(15000, 25000, ActivityType.BREAK);
        assertTrue(interval5.overlapsWith(interval6));

        // Test one contains another
        ActivityInterval interval7 = new ActivityInterval(10000, 30000, ActivityType.WORK);
        ActivityInterval interval8 = new ActivityInterval(15000, 20000, ActivityType.BREAK);
        assertTrue(interval7.overlapsWith(interval8));
    }


}
