package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OvertimeDTO {

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int breakTime;
    private String reason;

    private List<String> workTypes = new ArrayList<>();

    public void classifyWorkTypes() {
        Set<String> types = new HashSet<>();

        LocalDateTime cursor = startAt;
        while (cursor.isBefore(endAt)) {
            DayOfWeek day = cursor.getDayOfWeek();
            int hour = cursor.getHour();

            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                types.add("HOLIDAY");
            } else if (hour >= 22 || hour < 6) {
                types.add("NIGHT");
            } else if (hour >= 18) {
                types.add("OVERTIME");
            }

            cursor = cursor.plusMinutes(1);
        }

        this.workTypes = new ArrayList<>(types);
    }
}
