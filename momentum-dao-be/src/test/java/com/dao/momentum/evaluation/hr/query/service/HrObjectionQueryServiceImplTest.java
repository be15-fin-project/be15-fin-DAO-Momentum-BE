package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.HrObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.*;
import com.dao.momentum.evaluation.hr.query.mapper.HrObjectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        req = new HrObjectionListRequestDto();
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("정상: 전체 건수와 리스트 반환 시 HrObjectionListResultDto 생성")
    void getObjections_success() {
        // given
        long total = 15;
        HrObjectionItemDto dto1 = HrObjectionItemDto.builder()
                .objectionId(1L)
                .roundNo(2)
                .employeeName("reason1")  // 적절한 필드에 매핑
                .status(null)
                .build();
        HrObjectionItemDto dto2 = HrObjectionItemDto.builder()
                .objectionId(2L)
                .roundNo(2)
                .employeeName("reason2")
                .status(null)
                .build();

        given(mapper.countObjections(req)).willReturn(total);
        given(mapper.findObjections(req)).willReturn(List.of(dto1, dto2));

        // when
        HrObjectionListResultDto result = service.getObjections(req);

        // then
        assertThat(result.getList())
                .hasSize(2)
                .usingElementComparatorOnFields("objectionId", "roundNo", "employeeName", "status")
                .containsExactly(dto1, dto2);
        assertThat(result.getPagination().getTotalPage()).isEqualTo(2);
    }


    @Test
    @DisplayName("리스트가 null인 경우 HrException 발생")
    void getObjections_nullList_throwsHrException() {
        // given
        given(mapper.countObjections(req)).willReturn(5L);
        given(mapper.findObjections(req)).willReturn(null);

        // when
        Throwable thrown = catchThrowable(() -> service.getObjections(req));

        // then
        assertThat(thrown)
                .isInstanceOf(HrException.class)
                .hasMessageContaining(ErrorCode.HR_OBJECTIONS_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("리스트가 비어있을 때 빈 결과를 반환")
    void getObjections_emptyList_returnsEmptyResult() {
        // given
        given(mapper.countObjections(req)).willReturn(0L);
        given(mapper.findObjections(req)).willReturn(Collections.emptyList());

        // when
        HrObjectionListResultDto result = service.getObjections(req);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getList()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isZero();
        assertThat(result.getPagination().getTotalPage()).isZero();
    }

    @Test
    @DisplayName("정상: 이의제기 상세 조회 성공 시 ObjectionDetailResultDto 생성")
    void getObjectionDetail_success() {
        // given
        Long objectionId = 1L;
        Long resultId = 100L;

        ObjectionItemDto itemDto = ObjectionItemDto.builder()
                .objectionId(objectionId)
                .resultId(resultId)
                .empNo("20250001")
                .empName("홍길동")
                .build();

        List<FactorScoreDto> factorScores = Arrays.asList(
                new FactorScoreDto("11", "업무 수행 역량", "우수"),
                new FactorScoreDto("12", "협업 역량", "보통")
        );

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
    @DisplayName("비정상: 이의제기 상세 조회 실패 시 HrException 발생")
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
}
