package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.SimplePage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PaginationUtil {
    public static <T, R>SimplePage<R> wrapInPage(Page<T> page, Function<T, R> mapper){
        List<R> dto = page.getContent().stream()
                        .map(mapper)
                        .toList();
        return new SimplePage<>(
                dto,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );
    }
}
