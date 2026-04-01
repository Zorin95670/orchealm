package com.orchealm.orchealmapi.model.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    String sortBy,
    String direction
) {
    public static <T> PageResponse<T> of(final Page<T> page, final Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().findFirst().orElse(null);
        
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            order != null ? order.getProperty() : null,
            order != null ? order.getDirection().name() : null
        );
    }
}
