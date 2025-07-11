package com.dao.momentum.evaluation.eval.query.util;

public class EvaluationScoreAdjuster {

    /**
     * 평균 점수에 따라 감점/가점 환산
     *  score 평균 점수 (0 ~ 100)
     *  fullScore 항목의 만점 (ex. 10점, 8점, 5점 등)
     *  조정된 점수 (예: -2.5, +1.0 등)
     */
    public static double adjust(double score, double fullScore) {
        if (score >= 95) return scale(+2.0, fullScore);
        if (score >= 85) return 0.0;
        if (score >= 75) return scale(-2.5, fullScore);
        if (score >= 65) return scale(-5.0, fullScore);
        if (score >= 55) return scale(-7.5, fullScore);
        return scale(-10.0, fullScore);
    }

    private static double scale(double base, double fullScore) {
        return Math.round((base * fullScore / 10.0) * 100.0) / 100.0;
    }

    private EvaluationScoreAdjuster() {
        // 유틸 클래스이므로 생성자 private
    }
}
