package co.com.mercadolibre.bitly.application.usecases;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.model.ShortUrlStatistics;
import co.com.mercadolibre.bitly.domain.port.in.CreateShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import co.com.mercadolibre.bitly.insfrastructure.util.ShortenAlgorithm;
import java.time.LocalDateTime;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class CreateShortUrlUseCaseImpl implements CreateShortUrlUseCase {

  private final FindShortUrlUseCase findShortUrlUseCase;
  private final ShortUrlRepositoryOut repository;

  public CreateShortUrlUseCaseImpl(FindShortUrlUseCase findShortUrlUseCase,
      ShortUrlRepositoryOut repository) {
    this.findShortUrlUseCase = findShortUrlUseCase;
    this.repository = repository;
  }

  @Override
  public Mono<String> create(String url) {
    var baseUrl = url.toLowerCase().strip();
    return this.findShortUrlUseCase.findByOriginalUrl(baseUrl)
        .map(ShortUrl::hash)
        .onErrorResume((Throwable unused) -> generateHash(baseUrl)
            .flatMap((String generatedHash) -> createShortUrl(baseUrl, generatedHash))
            .flatMap(this.repository::save)
            .map(ShortUrl::hash));
  }

  public Mono<String> generateHash(String url) {
    var hash = ShortenAlgorithm.generateHash(url);
    return this.findShortUrlUseCase.existsByHash(hash)
        .flatMap((Boolean exists) -> Boolean.TRUE.equals(exists)
            ? generateHash(url) : Mono.just(hash));
  }

  private Mono<ShortUrl> createShortUrl(String originalUrl, String generatedHash) {
    var currentDateTime = LocalDateTime.now();
    var statistics = ShortUrlStatistics.builder()
        .creationAt(currentDateTime)
        .updatedAt(currentDateTime)
        .amountInteractions(0)
        .build();
    var shortUrl = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl(originalUrl)
        .hash(generatedHash)
        .statistics(statistics)
        .build();
    return Mono.just(shortUrl);
  }

}
