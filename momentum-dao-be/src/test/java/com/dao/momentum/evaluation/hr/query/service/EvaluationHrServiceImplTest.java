package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.FactorScoreDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;
import com.dao.momentum.evaluation.hr.query.mapper.EvaluationHrMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EvaluationHrServiceImplTest {

    @Mock
    private EvaluationHrMapper mapper;

    @InjectMocks
    private EvaluationHrServiceImpl service;

    private MyHrEvaluationListRequestDto req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        req = new MyHrEvaluationListRequestDto();
        req.setStartDate(null);
        req.setEndDate(null);
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("정상: 평가 내역과 요인점수, 페이징 정보 반환")
    void getHrEvaluations_success() {
        // given
        HrEvaluationItemDto item = HrEvaluationItemDto.builder()
                .roundNo(5)
                .resultId(123L)
                .overallGrade("우수")
                .evaluatedAt(LocalDateTime.of(2025,6,15,14,23,45))
                .build();
        // resultId 필드를 테스트에 반영하려면 DTO에 setter 또는 생성자에 포함되어야 합니다.
        // 가정: builder()에 resultId(long) 메서드도 있음
        // 예: item.setResultId(123L); 또는 builder().resultId(123L)
        // 여기서는 리플렉션 대신 가정적으로 setter 호출:

        long total = 1L;
        List<HrEvaluationItemDto> items = List.of(item);

        FactorScoreDto fs1 = FactorScoreDto.builder()
                .propertyName("커뮤니케이션")
                .score(88)
                .build();
        FactorScoreDto fs2 = FactorScoreDto.builder()
                .propertyName("문제해결")
                .score(92)
                .build();

        given(mapper.findHrEvaluations(1L, req)).willReturn(items);
        given(mapper.countHrEvaluations(1L, req)).willReturn(total);
        given(mapper.findFactorScores(123L)).willReturn(List.of(fs1, fs2));

        // when
        HrEvaluationListResultDto result = service.getHrEvaluations(1L, req);

        // then
        // items 검증
        assertThat(result.getItems()).hasSize(1);
        HrEvaluationItemDto actualItem = result.getItems().get(0);
        assertThat(actualItem.getRoundNo()).isEqualTo(5);
        assertThat(actualItem.getOverallGrade()).isEqualTo("우수");
        assertThat(actualItem.getEvaluatedAt()).isEqualTo(LocalDateTime.of(2025,6,15,14,23,45));

        // factorScores 검증
        assertThat(result.getFactorScores())
                .extracting(FactorScoreDto::getPropertyName, FactorScoreDto::getScore)
                .containsExactlyInAnyOrder(
                        tuple("커뮤니케이션", 88),
                        tuple("문제해결", 92)
                );

        // pagination 검증
        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isEqualTo(total);
        assertThat(p.getTotalPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 결과: items, factorScores 모두 빈 리스트, pagination만 생성")
    void getHrEvaluations_empty() {
        // given
        given(mapper.findHrEvaluations(1L, req)).willReturn(List.of());
        given(mapper.countHrEvaluations(1L, req)).willReturn(0L);

        // when
        HrEvaluationListResultDto result = service.getHrEvaluations(1L, req);

        // then
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getFactorScores()).isEmpty();

        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isZero();
        assertThat(p.getTotalPage()).isZero();
    }
}
