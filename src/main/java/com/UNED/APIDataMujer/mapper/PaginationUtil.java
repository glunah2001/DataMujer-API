package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.SimplePage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Clase utilitaria para paginación en resultados multiples de cualquier recurso.
 * Se usa para reducir metadata innecesaria y verbosa.
 * @author AHKolodin
 * */
public class PaginationUtil {

    /**
     * Función principal para paginación.
     * @param page pagina con contenido y metadata.
     * @param mapper función mapper.
     * @return paginación sencilla para enviar al cliente.
     * */
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
