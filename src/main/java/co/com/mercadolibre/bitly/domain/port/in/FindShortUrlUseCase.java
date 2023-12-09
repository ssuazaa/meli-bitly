package co.com.mercadolibre.bitly.domain.port.in;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindShortUrlUseCase {

  Flux<ShortUrl> findAll();

  Mono<ShortUrl> findByOriginalUrl(String originalUrl);

  Mono<ShortUrl> findByHash(String hash);

  Mono<Boolean> existsByHash(String hash);

}
