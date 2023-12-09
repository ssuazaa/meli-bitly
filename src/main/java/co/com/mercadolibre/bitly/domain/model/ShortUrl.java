package co.com.mercadolibre.bitly.domain.model;

import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record ShortUrl(UUID id,
                       String originalUrl,
                       String hash,
                       ShortUrlStatistics statistics) {

}
