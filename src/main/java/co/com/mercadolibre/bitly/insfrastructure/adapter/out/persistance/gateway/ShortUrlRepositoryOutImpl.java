package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.gateway;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.adapter.ShortUrlRepositoryMongoDb;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ShortUrlRepositoryOutImpl implements ShortUrlRepositoryOut {

  private final ShortUrlRepositoryMongoDb repository;

  public ShortUrlRepositoryOutImpl(ShortUrlRepositoryMongoDb repository) {
    this.repository = repository;
  }

  @Override
  public Flux<ShortUrl> findAll() {
    return this.repository.findAll();
  }

  @Override
  public Mono<ShortUrl> findByOriginalUrl(String originalUrl) {
    return this.repository.findByOriginalUrl(originalUrl);
  }

  @Override
  public Mono<ShortUrl> findByHash(String hash) {
    return this.repository.findByHash(hash);
  }

  @Override
  public Mono<Boolean> existsByHash(String hash) {
    return this.repository.existsByHash(hash);
  }

  @Override
  public Mono<ShortUrl> save(ShortUrl shortUrl) {
    return this.repository.save(shortUrl);
  }

}
