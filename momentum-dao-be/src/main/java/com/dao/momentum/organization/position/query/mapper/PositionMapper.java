package com.dao.momentum.organization.position.query.mapper;

import com.dao.momentum.organization.position.query.dto.response.PositionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PositionMapper {

    List<PositionDto> findPositions();
}
