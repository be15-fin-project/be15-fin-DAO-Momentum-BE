package com.dao.momentum.approve.query.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
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
