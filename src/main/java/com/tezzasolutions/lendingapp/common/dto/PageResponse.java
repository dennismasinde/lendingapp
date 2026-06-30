package com.tezzasolutions.lendingapp.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
        response.setLast(pageNumber >= response.getTotalPages() - 1);
        return response;
    }
}
