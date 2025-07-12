package com.dao.momentum.evaluation.kpi.query.util;

public class KpiPenaltyCalculator {

    public static int calculateTotalPenalty(int startedCount, int emptyMonths, double avgProgress, int underAchievedCount) {
        double penalty = 0;

        // ① 등록 KPI 수
        if (startedCount <= 1) penalty -= 2;
        else if (startedCount <= 3) penalty -= 1;

        // ② 무기록 월 수
        if (emptyMonths >= 5) penalty -= 2;
        else if (emptyMonths >= 3) penalty -= 1;

        // ③ 평균 달성률
        if (avgProgress < 50) penalty -= 2;
        else if (avgProgress < 70) penalty -= 1.5;
        else if (avgProgress < 80) penalty -= 1;

        // ④ 미달성 KPI 수
        if (underAchievedCount >= 3) penalty -= 2;
        else if (underAchievedCount >= 1) penalty -= 1;

        return (int) Math.round(penalty);
    }
}
