package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShortenerResponseStatisticsDto(LocalDateTime creationAt,
                                             LocalDateTime updatedAt,
                                             Integer amountInteractions,
                                             List<LocalDateTime> interactions) {

}
