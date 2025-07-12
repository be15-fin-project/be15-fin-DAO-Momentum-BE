package com.dao.momentum.evaluation.eval.query.service;

import com.dao.momentum.evaluation.eval.query.mapper.EvaluationScoreMapper;
import com.dao.momentum.evaluation.eval.query.util.EvaluationScoreAdjuster;
import com.dao.momentum.evaluation.hr.query.dto.response.RateInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationScoreServiceImpl implements EvaluationScoreService {

    private final EvaluationScoreMapper scoreMapper;

    @Override
    public int getAdjustedScoreForForm(int formId, Long empId, double fullScore, int year, int month) {
        log.info("getAdjustedScoreForForm 호출 - formId={}, empId={}, fullScore={}, year={}, month={}", formId, empId, fullScore, year, month);

        Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
        log.info("가장 최근 회차 ID: {}", roundId);

        if (roundId == null) {
            int adjusted = (int) Math.round(EvaluationScoreAdjuster.adjust(80.0, fullScore));
            log.info("회차 없음. 기본 점수 80으로 조정 결과: {}", adjusted);
            return adjusted;
        }

        Integer score = scoreMapper.findScoreByRoundId(roundId, empId);
        double base = (score != null) ? score : 80.0;
        int adjusted = (int) Math.round(EvaluationScoreAdjuster.adjust(base, fullScore));
        log.info("조회된 평가 점수: {}, 조정 점수: {}", base, adjusted);
        return adjusted;
    }

    @Override
    public int getAdjustedScoreForForms(List<Integer> formIds, Long empId, double fullScore, int year, int month) {
        log.info("getAdjustedScoreForForms 호출 - formIds={}, empId={}, fullScore={}, year={}, month={}", formIds, empId, fullScore, year, month);

        List<Integer> scores = formIds.stream()
                .map(formId -> {
                    Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
                    Integer score = (roundId != null) ? scoreMapper.findScoreByRoundId(roundId, empId) : null;
                    log.info("폼 {}: 회차 ID={}, 점수={}", formId, roundId, score);
                    return score;
                })
                .filter(score -> score != null)
                .collect(Collectors.toList());

        double avg = scores.stream().mapToInt(i -> i).average().orElse(80.0);
        int adjusted = (int) Math.round(EvaluationScoreAdjuster.adjust(avg, fullScore));
        log.info("평균 점수: {}, 조정된 점수: {}", avg, adjusted);
        return adjusted;
    }

    @Override
    public int getAverageAdjustedScoreForForms(List<Integer> formIds, Long empId, double totalScore, int year, int month) {
        log.info("getAverageAdjustedScoreForForms 호출 - formIds={}, empId={}, totalScore={}, year={}, month={}", formIds, empId, totalScore, year, month);

        List<Integer> scores = formIds.stream()
                .map(formId -> {
                    Long roundId = scoreMapper.findLatestRoundIdBefore(formId, year, month);
                    Integer score = (roundId != null) ? scoreMapper.findScoreByRoundId(roundId, empId) : null;
                    log.info("폼 {}: 회차 ID={}, 점수={}", formId, roundId, score);
                    return score;
                })
                .filter(score -> score != null)
                .collect(Collectors.toList());

        double avg = scores.stream().mapToInt(i -> i).average().orElse(80.0);
        int adjusted = (int) Math.round(EvaluationScoreAdjuster.adjust(avg, totalScore));
        log.info("평균 점수: {}, 조정된 총점: {}", avg, adjusted);
        return adjusted;
    }

    @Override
    public int getHrGradeDropPenalty(Long empId, int year, int month) {
        log.info("getHrGradeDropPenalty 호출 - empId={}, year={}, month={}", empId, year, month);

        List<Long> recentRounds = scoreMapper.findRecentHrRoundIdsBefore(empId, year, month);
        log.info("최근 HR 평가 회차 ID 목록: {}", recentRounds);
        if (recentRounds.size() < 2) return 0;

        Long round1 = recentRounds.get(0);
        Long round2 = recentRounds.get(1);

        Integer myScore1 = scoreMapper.findHrScoreByRoundIdAndEmpId(round1, empId);
        Integer myScore2 = scoreMapper.findHrScoreByRoundIdAndEmpId(round2, empId);
        log.info("나의 점수 - 회차1: {}점, 회차2: {}점", myScore1, myScore2);

        if (myScore1 == null || myScore2 == null) return 0;

        List<Integer> allScores1 = scoreMapper.findAllHrScoresByRoundId(round1);
        List<Integer> allScores2 = scoreMapper.findAllHrScoresByRoundId(round2);
        log.info("전체 점수 수 - 회차1: {}, 회차2: {}", allScores1.size(), allScores2.size());

        if (allScores1.isEmpty() || allScores2.isEmpty()) return 0;

        RateInfo rateInfo1 = scoreMapper.findRateInfoByRoundId(round1);
        RateInfo rateInfo2 = scoreMapper.findRateInfoByRoundId(round2);
        log.info("등급 비율 정보 - 회차1: {}, 회차2: {}", rateInfo1, rateInfo2);

        String grade1 = calculateRelativeGrade(allScores1, myScore1, rateInfo1);
        String grade2 = calculateRelativeGrade(allScores2, myScore2, rateInfo2);
        log.info("등급 변화: 이전 등급={}, 현재 등급={}", grade2, grade1);

        int drop = getGradeDrop(grade1, grade2);
        log.info("등급 하락 수: {}, 감점: {}", drop, -1 * drop);
        return -1 * drop;
    }

    private String calculateRelativeGrade(List<Integer> allScores, int myScore, RateInfo rateInfo) {
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

        log.info("전체 인원: {}, 내 순위: {}, 백분위: {}", total, rank, percentile);
        log.info("등급 기준 - S: {}%, A: {}%, B: {}%, C: {}%, D: 나머지", s, a, b, c);

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
        int drop = (prevIdx == -1 || currIdx == -1) ? 0 : Math.max(0, currIdx - prevIdx);
        log.info("등급 인덱스 - 이전: {}, 현재: {}, 하락 수: {}", prevIdx, currIdx, drop);
        return drop;
    }
}
