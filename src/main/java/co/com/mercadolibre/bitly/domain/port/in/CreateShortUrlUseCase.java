package co.com.mercadolibre.bitly.domain.port.in;

import reactor.core.publisher.Mono;

public interface CreateShortUrlUseCase {

  Mono<String> create(String url);

}
