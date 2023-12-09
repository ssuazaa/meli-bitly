package co.com.mercadolibre.bitly.application.usecases;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import co.com.mercadolibre.bitly.insfrastructure.config.exceptions.ObjectNotFoundException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class FindShortUrlUseCaseImpl implements FindShortUrlUseCase {

  private final ShortUrlRepositoryOut repository;

  public FindShortUrlUseCaseImpl(ShortUrlRepositoryOut repository) {
    this.repository = repository;
  }

  @Override
  public Flux<ShortUrl> findAll() {
    return repository.findAll();
  }

  @Override
  public Mono<ShortUrl> findByOriginalUrl(String originalUrl) {
    var baseUrl = getBaseText(originalUrl);
    return this.repository.findByOriginalUrl(baseUrl)
        .switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("SHORT_URL_NOT_FOUND",
            String.format("Short with original url '%s' was not found", baseUrl))));
  }

  @Override
  public Mono<ShortUrl> findByHash(String hash) {
    var baseHash = getBaseText(hash);
    return this.repository.findByHash(baseHash)
        .publishOn(Schedulers.boundedElastic())
        .flatMap((ShortUrl shortUrlDb) -> {
          var newShortUrl = addInteraction(shortUrlDb);
          return this.repository.save(newShortUrl);
        })
        .switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("SHORT_URL_NOT_FOUND",
            String.format("Short with hash '%s' was not found", baseHash))));
  }

  @Override
  public Mono<Boolean> existsByHash(String hash) {
    return this.repository.existsByHash(getBaseText(hash));
  }

  private String getBaseText(String text) {
    return text.toLowerCase().strip();
  }

  private ShortUrl addInteraction(ShortUrl shortUrl) {
    var currentDateTime = LocalDateTime.now();
    var newAmountInteractions = new AtomicInteger(
        shortUrl.statistics().amountInteractions()).incrementAndGet();
    var statistics = shortUrl.statistics().toBuilder()
        .updatedAt(currentDateTime)
        .amountInteractions(newAmountInteractions)
        .build();
    return shortUrl.toBuilder()
        .statistics(statistics)
        .build();
  }

}
