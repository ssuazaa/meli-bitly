package co.com.mercadolibre.bitly.insfrastructure.config.errorhandler;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ErrorResponseDto(String key,
                               String message,
                               LocalDateTime dateTime) {

}
