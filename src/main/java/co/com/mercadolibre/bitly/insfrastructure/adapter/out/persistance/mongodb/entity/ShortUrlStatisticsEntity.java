package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.entity;

import java.time.LocalDateTime;

public record ShortUrlStatisticsEntity(LocalDateTime creationAt,
                                       LocalDateTime updatedAt,
                                       Integer amountInteractions) {

}
