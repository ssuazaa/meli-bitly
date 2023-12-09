package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.entity;

import java.util.UUID;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "shorten_urls")
@Builder
public record ShortUrlEntity(@MongoId UUID id,
                             String originalUrl,
                             String hash,
                             ShortUrlStatisticsEntity statistics) {

}
