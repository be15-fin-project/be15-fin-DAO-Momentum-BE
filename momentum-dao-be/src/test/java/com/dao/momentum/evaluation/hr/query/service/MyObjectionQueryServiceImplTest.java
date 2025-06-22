package com.dao.momentum.evaluation.hr.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.hr.exception.HrException;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionItemDto;
import com.dao.momentum.evaluation.hr.query.dto.response.MyObjectionListResultDto;
import com.dao.momentum.evaluation.hr.query.dto.request.MyObjectionRaw;
import com.dao.momentum.evaluation.hr.query.mapper.MyObjectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
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
        req = new MyObjectionListRequestDto();
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("정상: total>0 rawList 반환 시 MyObjectionListResultDto 생성")
    void getMyObjections_success() {
        long total = 3L;
        MyObjectionRaw raw1 = new MyObjectionRaw();
        raw1.setObjectionId(101L);
        raw1.setCreatedAt("2025-06-22 17:00:00");
        raw1.setOverallScore(95);
        raw1.setStatusType("PENDING");

        MyObjectionRaw raw2 = new MyObjectionRaw();
        raw2.setObjectionId(102L);
        raw2.setCreatedAt("2025-06-21 15:30:00");
        raw2.setOverallScore(82);
        raw2.setStatusType("APPROVED");

        List<MyObjectionRaw> rawList = List.of(raw1, raw2);

        given(mapper.countMyObjections(1L, req)).willReturn(total);
        given(mapper.findMyObjections(1L, req)).willReturn(rawList);

        MyObjectionListResultDto result = service.getMyObjections(1L, req);

        assertThat(result.getContent()).hasSize(2);
        MyObjectionItemDto item1 = result.getContent().get(0);
        assertThat(item1.getObjectionId()).isEqualTo(101L);
        assertThat(item1.getCreatedAt()).isEqualTo("2025-06-22 17:00:00");
        assertThat(item1.getOverallGrade()).isEqualTo("S");
        assertThat(item1.getStatusType()).isEqualTo("PENDING");

        MyObjectionItemDto item2 = result.getContent().get(1);
        assertThat(item2.getObjectionId()).isEqualTo(102L);
        assertThat(item2.getOverallGrade()).isEqualTo("A");
        assertThat(item2.getStatusType()).isEqualTo("APPROVED");

        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isEqualTo(total);
        assertThat(p.getTotalPage()).isEqualTo((int)Math.ceil((double)total/req.getSize()));
    }

    @Test
    @DisplayName("total==0 일 때 HrException 발생")
    void getMyObjections_totalZero_throws() {
        given(mapper.countMyObjections(1L, req)).willReturn(0L);

        assertThatThrownBy(() -> service.getMyObjections(1L, req))
                .isInstanceOf(HrException.class)
                .hasMessageContaining("조회 가능한 인사 평가 이의제기 내역이 없습니다.");
    }

    @Test
    @DisplayName("rawList null 일 때 HrException 발생")
    void getMyObjections_rawListNull_throws() {
        given(mapper.countMyObjections(1L, req)).willReturn(5L);
        given(mapper.findMyObjections(1L, req)).willReturn(null);

        assertThatThrownBy(() -> service.getMyObjections(1L, req))
                .isInstanceOf(HrException.class)
                .hasMessageContaining("조회 가능한 인사 평가 이의제기 내역이 없습니다.");
    }

    @Test
    @DisplayName("rawList empty 일 때 HrException 발생")
    void getMyObjections_rawListEmpty_throws() {
        given(mapper.countMyObjections(1L, req)).willReturn(5L);
        given(mapper.findMyObjections(1L, req)).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getMyObjections(1L, req))
                .isInstanceOf(HrException.class)
                .hasMessageContaining("조회 가능한 인사 평가 이의제기 내역이 없습니다.");
    }
}
