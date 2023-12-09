package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.adapter;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.config.MongoDBRepositoryConfig;
import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.entity.ShortUrlEntity;
import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.mapper.MongoDBShortUrlEntityMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ShortUrlRepositoryMongoDb {

  private final MongoDBShortUrlEntityMapper mapper;
  private final MongoDBRepositoryConfig repository;

  public ShortUrlRepositoryMongoDb(MongoDBShortUrlEntityMapper mapper,
      MongoDBRepositoryConfig repository) {
    this.mapper = mapper;
    this.repository = repository;
  }

  public Flux<ShortUrl> findAll() {
    return this.repository.findAll()
        .map(this.mapper::toDomain);
  }

  public Mono<ShortUrl> findByOriginalUrl(String originalUrl) {
    return this.repository.findOne(Example.of(ShortUrlEntity.builder()
            .originalUrl(originalUrl)
            .build()))
        .map(this.mapper::toDomain);
  }

  public Mono<ShortUrl> findByHash(String hash) {
    return this.repository.findOne(Example.of(ShortUrlEntity.builder()
            .hash(hash)
            .build()))
        .map(this.mapper::toDomain);
  }

  public Mono<Boolean> existsByHash(String hash) {
    return this.repository.exists(Example.of(ShortUrlEntity.builder()
        .hash(hash)
        .build()));
  }

  public Mono<ShortUrl> save(ShortUrl shortUrl) {
    return this.repository.save(this.mapper.toEntity(shortUrl))
        .map(this.mapper::toDomain);
  }

}
