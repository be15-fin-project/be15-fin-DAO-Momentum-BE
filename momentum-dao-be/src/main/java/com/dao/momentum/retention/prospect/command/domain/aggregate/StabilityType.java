package com.dao.momentum.retention.prospect.command.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum StabilityType {
    SEVERE(0, "심각"),
    WARNING(40, "주의"),
    NORMAL(60, "보통"),
    GOOD(80, "양호");

    private final int threshold;  // 하한값
    private final String label;

    StabilityType(int threshold, String label) {
        this.threshold = threshold;
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static StabilityType from(String label) {
        for (StabilityType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }

    public static StabilityType fromScore(int score) {
        StabilityType result = SEVERE;
        for (StabilityType type : values()) {
            if (score >= type.threshold) {
                result = type;
            }
        }
        return result;
    }
}
