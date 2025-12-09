package com.mvs.product_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginationDto<T> {
    private List<T> content;
    private Page page;

    public PaginationDto(List<T> content, org.springframework.data.domain.Page page) {
        this.content = content;
        Page p = new Page();
        p.setPage(page.getNumber());
        p.setSize(page.getSize());
        p.setTotalElements(page.getTotalElements());
        p.setTotalPages(page.getTotalPages());
        p.setLastPage(page.isLast());
        this.page = p;
    }

    @Data
    public static class Page {
        private int page;
        private int size;
        private int totalPages;
        private long totalElements;
        private boolean lastPage;
    }
}
