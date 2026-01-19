package com.github.yaroslavvol.backend.repository;

import com.github.yaroslavvol.backend.model.ActivityInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalRepository extends JpaRepository<ActivityInterval, Long> {

    @Query("SELECT i FROM ActivityInterval i WHERE i.start < :end AND i.end > :start")
    List<ActivityInterval> findOverlappingIntervals(
            @Param("start") Integer start,
            @Param("end") Integer end
    );

    List<ActivityInterval> findAllByOrderByStartAsc();
}
