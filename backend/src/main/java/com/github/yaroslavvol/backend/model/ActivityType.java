package com.github.yaroslavvol.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityType {
    WORK("Работа"),
    BREAK("Перерыв");

    private final String displayName;
}
