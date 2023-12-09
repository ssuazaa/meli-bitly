package co.com.mercadolibre.bitly.domain.port.out;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShortUrlRepositoryOut {

  Flux<ShortUrl> findAll();

  Mono<ShortUrl> findByOriginalUrl(String originalUrl);

  Mono<ShortUrl> findByHash(String hash);

  Mono<Boolean> existsByHash(String hash);

  Mono<ShortUrl> save(ShortUrl shortUrl);

}
