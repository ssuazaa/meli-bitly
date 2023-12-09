package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto;

import co.com.mercadolibre.bitly.domain.model.ShortUrlStatistics;
import java.util.UUID;

public record ShortenerResponseDto(UUID id,
                                   String originalUrl,
                                   String hash,
                                   ShortUrlStatistics statistics) {

}
