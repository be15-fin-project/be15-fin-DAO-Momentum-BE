package com.dao.momentum.evaluation.eval.query.service;

import java.util.List;

public interface EvaluationScoreService {

    // 단일 평가 폼에 대해 감점/가점 환산 점수 반환
    int getAdjustedScoreForForm(int formId, Long empId, double fullScore);

    // 복수 평가 폼에 대해 평균 후 감점/가점 환산
    int getAdjustedScoreForForms(List<Integer> formIds, Long empId, double fullScore);

    // 복수 평가 폼의 평균 점수를 기준으로 총점 기준 환산 점수를 반환합니다.
    int getAverageAdjustedScoreForForms(List<Integer> formIds, Long empId, double totalScore);

    // 인사 평가의 최근 2회 평가 등급 하락 감점 반환
    int getHrGradeDropPenalty(Long empId);
}
