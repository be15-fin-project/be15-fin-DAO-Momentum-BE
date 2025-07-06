package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.MyObjectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class MyObjectionQueryServiceImplTest {

    @Mock
    private MyObjectionMapper mapper;

    @InjectMocks
    private MyObjectionQueryServiceImpl service;

    private MyObjectionListRequestDto req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        req = MyObjectionListRequestDto.builder()
                .page(1)
                .size(10)
                .build();
    }

    @Nested
    @DisplayName("getMyObjections: 이의제기 목록 조회")
    class GetMyObjections {

        @Test
        @DisplayName("정상: total > 0 rawList 반환 시 MyObjectionListResultDto 생성")
        void getMyObjections_success() {
            long total = 2L;
            MyObjectionRaw raw1 = MyObjectionRaw.builder()
                    .objectionId(101L)
                    .createdAt("2025-06-22 17:00:00")
                    .overallScore(95)
                    .statusType("PENDING")
                    .build();

            MyObjectionRaw raw2 = MyObjectionRaw.builder()
                    .objectionId(102L)
                    .createdAt("2025-06-21 15:30:00")
                    .overallScore(82)
                    .statusType("APPROVED")
                    .build();

            List<MyObjectionRaw> rawList = List.of(raw1, raw2);

            given(mapper.countMyObjections(1L, req)).willReturn(total);
            given(mapper.findMyObjections(1L, req)).willReturn(rawList);

            MyObjectionListResultDto result = service.getMyObjections(1L, req);

            assertThat(result.content()).hasSize(2);

            MyObjectionItemDto item1 = result.content().get(0);
            assertThat(item1.objectionId()).isEqualTo(101L);
            assertThat(item1.createdAt()).isEqualTo("2025-06-22 17:00:00");
            assertThat(item1.overallGrade()).isEqualTo("S");
            assertThat(item1.statusType()).isEqualTo("PENDING");

            MyObjectionItemDto item2 = result.content().get(1);
            assertThat(item2.objectionId()).isEqualTo(102L);
            assertThat(item2.overallGrade()).isEqualTo("B");
            assertThat(item2.statusType()).isEqualTo("APPROVED");

            Pagination p = result.pagination();
            assertThat(p.getCurrentPage()).isEqualTo(1);
            assertThat(p.getTotalItems()).isEqualTo(total);
            assertThat(p.getTotalPage()).isEqualTo((int) Math.ceil((double) total / req.size()));
        }

        @Test
        @DisplayName("rawList null 일 때 HrException 발생")
        void getMyObjections_rawListNull_throws() {
            given(mapper.countMyObjections(1L, req)).willReturn(5L);
            given(mapper.findMyObjections(1L, req)).willReturn(null);

            assertThatThrownBy(() -> service.getMyObjections(1L, req))
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.MY_OBJECTIONS_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("getObjectionDetail: 이의제기 상세 조회")
    class GetObjectionDetail {

        @Test
        @DisplayName("정상: getObjectionDetail 호출 시 ObjectionDetailResultDto 생성")
        void getObjectionDetail_success() {
            // 기본 이의제기 항목
            ObjectionItemDto base = ObjectionItemDto.builder()
                    .objectionId(5001L)
                    .resultId(2001L)
                    .empNo("20250001")
                    .empName("김현우")
                    .evaluatedAt("2025-06-22 17:31:08")
                    .objectionReason("점수가 낮습니다.")
                    .statusType("PENDING")
                    .responseReason("재검토 요청")
                    .build();

            // 요인별 점수
            List<FactorScoreDto> scores = List.of(
                    FactorScoreDto.builder().propertyName("커뮤니케이션").score("A").build(),
                    FactorScoreDto.builder().propertyName("태도").score("B").build()
            );

            // 가중치
            WeightInfo weightInfo = WeightInfo.builder()
                    .weightPerform(20)
                    .weightTeam(15)
                    .weightAttitude(15)
                    .weightGrowth(25)
                    .weightEngagement(20)
                    .weightResult(5)
                    .build();

            // 등급 비율
            RateInfo rateInfo = RateInfo.builder()
                    .rateS(5)
                    .rateA(20)
                    .rateB(35)
                    .rateC(30)
                    .rateD(10)
                    .build();

            given(mapper.findObjectionDetail(5001L)).willReturn(base);
            given(mapper.findFactorScores(2001L)).willReturn(scores);
            given(mapper.findWeightInfo(2001L)).willReturn(weightInfo);
            given(mapper.findRateInfo(2001L)).willReturn(rateInfo);

            ObjectionDetailResultDto dto = service.getObjectionDetail(5001L);

            // itemDto 확인
            ObjectionItemDto got = dto.itemDto();
            assertThat(got.objectionId()).isEqualTo(5001L);
            assertThat(got.resultId()).isEqualTo(2001L);

            // 요인별 점수
            assertThat(dto.factorScores()).hasSize(2);
            assertThat(dto.factorScores().get(0).propertyName()).isEqualTo("커뮤니케이션");
            assertThat(dto.factorScores().get(0).score()).isEqualTo("A");

            // 가중치
            assertThat(dto.weightInfo().weightPerform()).isEqualTo(20);
            assertThat(dto.weightInfo().weightResult()).isEqualTo(5);

            // 등급 비율
            assertThat(dto.rateInfo().rateS()).isEqualTo(5);
            assertThat(dto.rateInfo().rateB()).isEqualTo(35);
        }

        @Test
        @DisplayName("findObjectionDetail null 일 때 HrException 발생")
        void getObjectionDetail_notFound_throws() {
            given(mapper.findObjectionDetail(5001L)).willReturn(null);

            assertThatThrownBy(() -> service.getObjectionDetail(5001L))
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.MY_OBJECTIONS_NOT_FOUND.getMessage());
        }
    }
}
