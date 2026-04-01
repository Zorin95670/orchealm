package com.orchealm.orchealmapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

/**
 * Interface for resolving the appropriate HTTP status code to return in responses containing paged resources.
 * Implementations can customize the logic to determine whether a full or partial content status should be returned
 * based on pagination details.
 */
public interface PagedResponseStatusResolver {
    /**
     * Determines the HTTP status code for a paged response. Returns 206 (Partial Content) if multiple pages exist,
     * otherwise 200 (OK).
     *
     * @param resources the page of resources to check
     * @param <T>       the type of resource contained in the page
     * @return the HTTP status code to use for the response
     */
    default <T> int getStatus(final Page<T> resources) {
        if (resources.getTotalPages() > 1) {
            return HttpStatus.PARTIAL_CONTENT.value();
        }

        return HttpStatus.OK.value();
    }
}
