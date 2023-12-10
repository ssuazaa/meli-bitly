package co.com.mercadolibre.bitly.insfrastructure.config.errorhandler;

import co.com.mercadolibre.bitly.insfrastructure.config.exceptions.BaseException;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(value = {BaseException.class})
  public Mono<ResponseEntity<ErrorResponseDto>> handleBaseException(BaseException ex) {
    var error = ErrorResponseDto.builder()
        .key(ex.getKey())
        .message(ex.getMessage())
        .dateTime(LocalDateTime.now())
        .build();
    return Mono.just(ResponseEntity
        .status(ex.getStatusCode())
        .body(error));
  }

}
