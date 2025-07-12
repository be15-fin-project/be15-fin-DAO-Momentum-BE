package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.mapper.EvaluationScoreMapper;
import com.dao.momentum.evaluation.eval.query.util.EvaluationScoreAdjuster;
import com.dao.momentum.evaluation.hr.query.dto.response.RateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationScoreServiceImpl implements EvaluationScoreService {

    private final EvaluationScoreMapper scoreMapper;

    @Override
    public int getAdjustedScoreForForm(int formId, Long empId, double fullScore, int year, int month) {
        Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
        if (roundId == null) return (int) Math.round(EvaluationScoreAdjuster.adjust(100.0, fullScore));

        Integer score = scoreMapper.findScoreByRoundId(roundId, empId);
        double base = (score != null) ? score : 100.0;
        return (int) Math.round(EvaluationScoreAdjuster.adjust(base, fullScore));
    }

    @Override
    public int getAdjustedScoreForForms(List<Integer> formIds, Long empId, double fullScore, int year, int month) {
        List<Integer> scores = formIds.stream()
                .map(formId -> {
                    Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
                    return (roundId != null) ? scoreMapper.findScoreByRoundId(roundId, empId) : null;
                })
                .filter(score -> score != null)
                .collect(Collectors.toList());

        double avg = scores.stream().mapToInt(i -> i).average().orElse(100.0);
        return (int) Math.round(EvaluationScoreAdjuster.adjust(avg, fullScore));
    }

    @Override
    public int getAverageAdjustedScoreForForms(List<Integer> formIds, Long empId, double totalScore, int year, int month) {
        List<Integer> scores = formIds.stream()
                .map(formId -> {
                    Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
                    return (roundId != null) ? scoreMapper.findScoreByRoundId(roundId, empId) : null;
                })
                .filter(score -> score != null)
                .collect(Collectors.toList());

        double avg = scores.stream().mapToInt(i -> i).average().orElse(100.0);
        return (int) Math.round(EvaluationScoreAdjuster.adjust(avg, totalScore));
    }

    @Override
    public int getHrGradeDropPenalty(Long empId, int year, int month) {
        List<Long> recentRounds = scoreMapper.findRecentHrRoundIdsBefore(empId, year, month);
        if (recentRounds.size() < 2) return 0;

        Long round1 = recentRounds.get(0); // 가장 최근
        Long round2 = recentRounds.get(1); // 그 이전

        Integer myScore1 = scoreMapper.findHrScoreByRoundIdAndEmpId(round1, empId);
        Integer myScore2 = scoreMapper.findHrScoreByRoundIdAndEmpId(round2, empId);
        if (myScore1 == null || myScore2 == null) return 0;

        List<Integer> allScores1 = scoreMapper.findAllHrScoresByRoundId(round1);
        List<Integer> allScores2 = scoreMapper.findAllHrScoresByRoundId(round2);
        if (allScores1.isEmpty() || allScores2.isEmpty()) return 0;

        RateInfo rateInfo1 = scoreMapper.findRateInfoByRoundId(round1);
        RateInfo rateInfo2 = scoreMapper.findRateInfoByRoundId(round2);

        String grade1 = calculateRelativeGrade(allScores1, myScore1, rateInfo1);
        String grade2 = calculateRelativeGrade(allScores2, myScore2, rateInfo2);

        int drop = getGradeDrop(grade1, grade2);
        return -1 * drop; // 1등급 하락당 -1점
    }

    private String calculateRelativeGrade(List<Integer> allScores, int myScore, RateInfo rateInfo) {
        if (allScores == null || allScores.isEmpty()) return "-";

        List<Integer> sorted = allScores.stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        int rank = sorted.indexOf(myScore) + 1;
        int total = sorted.size();
        double percentile = ((double) rank / total) * 100;

        int s = rateInfo.rateS();
        int a = rateInfo.rateA();
        int b = rateInfo.rateB();
        int c = rateInfo.rateC();
        int d = rateInfo.rateD();

        if (percentile <= s) return "S";
        if (percentile <= s + a) return "A";
        if (percentile <= s + a + b) return "B";
        if (percentile <= s + a + b + c) return "C";
        return "D";
    }

    private int getGradeDrop(String prev, String curr) {
        List<String> grades = List.of("S", "A", "B", "C", "D");
        int prevIdx = grades.indexOf(prev);
        int currIdx = grades.indexOf(curr);
        if (prevIdx == -1 || currIdx == -1) return 0;
        return Math.max(0, currIdx - prevIdx);
    }
}
