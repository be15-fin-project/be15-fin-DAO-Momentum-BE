package com.dao.momentum.approve.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Schema(description = "페이지 request")
public class PageRequest {

    @Min(value = 1)
    private Integer page;

    @Min(value = 1)
    private Integer size;

    public PageRequest(Integer page, Integer size) {
        this.page = (page == null) ? 1 : page;
        this.size = (size == null) ? 10 : size;
    }

    public int getOffset(){
        return (page-1) * size;
    }

    public int getLimit(){
        return size;
    }
}
