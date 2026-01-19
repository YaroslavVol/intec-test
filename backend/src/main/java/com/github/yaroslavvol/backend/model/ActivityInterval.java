package com.github.yaroslavvol.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "intervals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start time is required")
    @Min(value = 0, message = "Start must be >= 0")
    @Max(value = 86400, message = "Start must be <= 86400")
    @Column(name = "start_time", nullable = false)
    private Integer start;

    @NotNull(message = "End time is required")
    @Min(value = 0, message = "End must be >= 0")
    @Max(value = 86400, message = "End must be <= 86400")
    @Column(name = "end_time", nullable = false)
    private Integer end;

    @NotNull(message = "Activity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType type;

    public ActivityInterval(Integer start, Integer end, ActivityType type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public boolean overlapsWith(ActivityInterval other) {
        return this.start < other.end && this.end > other.start;
    }

    public String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @Override
    public String toString() {
        return formatTime(start) + " - " + formatTime(end) + " (" + type.getDisplayName() + ")";
    }
}
