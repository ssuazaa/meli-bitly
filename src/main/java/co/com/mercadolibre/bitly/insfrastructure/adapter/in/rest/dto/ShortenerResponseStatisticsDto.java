package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ShortenerResponseStatisticsDto(LocalDateTime creationAt,
                                             LocalDateTime updatedAt,
                                             Integer amountInteractions) {

}
