package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class HrObjectionQueryServiceImplTest {

    @Mock
    private HrObjectionMapper mapper;

    @InjectMocks
    private HrObjectionQueryServiceImpl service;

    private HrObjectionListRequestDto req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        req = HrObjectionListRequestDto.builder()
                .page(1)
                .size(10)
                .build();
    }

    @Nested
    @DisplayName("이의제기 목록 조회 테스트")
    class GetObjectionsTests {

        @Test
        @DisplayName("정상: 이의제기 목록 조회 성공 - HrObjectionListResultDto 생성")
        void getObjections_success() {
            // given
            long total = 2;
            HrObjectionItemDto dto1 = createHrObjectionItemDto(1L, 2, "reason1");
            HrObjectionItemDto dto2 = createHrObjectionItemDto(2L, 2, "reason2");

            // requesterEmpId 값을 명시적으로 설정
            req = HrObjectionListRequestDto.builder()
                    .requesterEmpId(1L)  // 필요한 필드 값을 명시적으로 설정
                    .page(1)
                    .size(10)
                    .build();

            given(mapper.countObjections(req)).willReturn(total);
            given(mapper.findObjections(req)).willReturn(Arrays.asList(dto1, dto2));  // 데이터 반환

            // when
            HrObjectionListResultDto result = service.getObjections(1L, req);

            // then
            assertThat(result.getList())
                    .hasSize(2)
                    .containsExactly(dto1, dto2);
            assertThat(result.getPagination().getTotalPage()).isEqualTo(1);  // 페이지는 1이어야 함
        }

        @Test
        @DisplayName("리스트가 비어있을 때 - 빈 결과 반환")
        void getObjections_emptyList_returnsEmptyResult() {
            // given
            given(mapper.countObjections(req)).willReturn(0L);
            given(mapper.findObjections(req)).willReturn(Collections.emptyList());

            // when
            HrObjectionListResultDto result = service.getObjections(1L, req);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getList()).isEmpty();
            assertThat(result.getPagination().getTotalItems()).isZero();
            assertThat(result.getPagination().getTotalPage()).isZero();
        }

        private HrObjectionItemDto createHrObjectionItemDto(Long objectionId, Integer roundNo, String employeeName) {
            return HrObjectionItemDto.builder()
                    .objectionId(objectionId)
                    .roundNo(roundNo)
                    .employeeName(employeeName)
                    .status(null)
                    .build();
        }
    }

    @Nested
    @DisplayName("이의제기 상세 조회 테스트")
    class GetObjectionDetailTests {

        @Test
        @DisplayName("정상: 이의제기 상세 조회 성공 - ObjectionDetailResultDto 생성")
        void getObjectionDetail_success() {
            // given
            Long objectionId = 1L;
            Long resultId = 100L;

            ObjectionItemDto itemDto = createObjectionItemDto(objectionId, resultId);
            List<FactorScoreDto> factorScores = createFactorScores();
            WeightInfo weightInfo = new WeightInfo(20, 20, 20, 20, 10, 10);
            RateInfo rateInfo = new RateInfo(10, 20, 30, 30, 10);

            given(mapper.findObjectionDetail(objectionId)).willReturn(itemDto);
            given(mapper.findFactorScores(resultId)).willReturn(factorScores);
            given(mapper.findWeightInfo(resultId)).willReturn(weightInfo);
            given(mapper.findRateInfo(resultId)).willReturn(rateInfo);

            // when
            ObjectionDetailResultDto result = service.getObjectionDetail(objectionId);

            // then
            assertThat(result.getItemDto()).isEqualTo(itemDto);
            assertThat(result.getFactorScores()).hasSize(2);
            assertThat(result.getWeightInfo()).isEqualTo(weightInfo);
            assertThat(result.getRateInfo()).isEqualTo(rateInfo);
        }

        @Test
        @DisplayName("비정상: 이의제기 상세 조회 실패 시 - HrException 발생")
        void getObjectionDetail_notFound_throwsHrException() {
            // given
            Long objectionId = 1L;
            given(mapper.findObjectionDetail(objectionId)).willReturn(null);

            // when
            Throwable thrown = catchThrowable(() -> service.getObjectionDetail(objectionId));

            // then
            assertThat(thrown)
                    .isInstanceOf(HrException.class)
                    .hasMessageContaining(ErrorCode.MY_OBJECTIONS_NOT_FOUND.getMessage());
        }

        private ObjectionItemDto createObjectionItemDto(Long objectionId, Long resultId) {
            return ObjectionItemDto.builder()
                    .objectionId(objectionId)
                    .resultId(resultId)
                    .empNo("20250001")
                    .empName("홍길동")
                    .build();
        }

        private List<FactorScoreDto> createFactorScores() {
            return Arrays.asList(
                    new FactorScoreDto("11", "업무 수행 역량", "우수"),
                    new FactorScoreDto("12", "협업 역량", "보통")
            );
        }
    }
}
