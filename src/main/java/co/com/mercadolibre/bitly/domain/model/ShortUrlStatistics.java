package co.com.mercadolibre.bitly.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record ShortUrlStatistics(LocalDateTime creationAt,
                                 LocalDateTime updatedAt,
                                 Integer amountInteractions) {

}
