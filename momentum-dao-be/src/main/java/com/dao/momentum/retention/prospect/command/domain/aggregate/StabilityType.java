package com.dao.momentum.retention.prospect.command.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum StabilityType {
    STABLE("안정형"),
    WARNING("주의형"),
    UNSTABLE("불안정형");

    private final String label;

    StabilityType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static StabilityType from(String value) {
        for (StabilityType type : StabilityType.values()) {
            if (type.label.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown stability type: " + value);
    }
}
