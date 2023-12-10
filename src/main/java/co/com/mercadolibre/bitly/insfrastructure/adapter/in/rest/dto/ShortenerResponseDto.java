package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ShortenerResponseDto(UUID id,
                                   String originalUrl,
                                   String hash,
                                   ShortenerResponseStatisticsDto statistics) {

}
