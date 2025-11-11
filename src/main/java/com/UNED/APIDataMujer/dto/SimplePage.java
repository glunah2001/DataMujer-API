package com.UNED.APIDataMujer.dto;

import java.util.List;

public record SimplePage<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage
) { }
