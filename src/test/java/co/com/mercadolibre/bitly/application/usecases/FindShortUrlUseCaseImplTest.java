package co.com.mercadolibre.bitly.application.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.model.ShortUrlStatistics;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import co.com.mercadolibre.bitly.insfrastructure.config.exceptions.ObjectNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FindShortUrlUseCaseImplTest {

  @MockBean
  ShortUrlRepositoryOut shortUrlRepositoryOut;

  FindShortUrlUseCase findShortUrlUseCase;

  @BeforeEach
  public void setUp() {
    this.shortUrlRepositoryOut = mock(ShortUrlRepositoryOut.class);
    this.findShortUrlUseCase = new FindShortUrlUseCaseImpl(this.shortUrlRepositoryOut);
  }

  @Test
  @DisplayName("testFindAll() -> Good case [not empty and empty]")
  void testFindAll() {
    // Assert
    var elements = List.of(ShortUrl.builder()
            .build(),
        ShortUrl.builder()
            .build());
    var findAllIterations = new LinkedList<>(List.of(Flux.fromIterable(elements),
        Flux.empty()));

    when(this.shortUrlRepositoryOut.findAll())
        .thenAnswer(invocationOnMock -> findAllIterations.poll());

    // Act
    var result1 = this.findShortUrlUseCase.findAll();
    var result2 = this.findShortUrlUseCase.findAll();

    // Arrange
    StepVerifier.create(result1)
        .expectNextCount(2)
        .verifyComplete();

    StepVerifier.create(result2)
        .expectNextCount(0)
        .verifyComplete();

    verify(this.shortUrlRepositoryOut, times(2)).findAll();
  }

  @Test
  @DisplayName("testFindByOriginalUrl() -> Good case [exists]")
  void testFindByOriginalUrl() {
    // Arrange
    var url = "https://com.google.com.co";
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl(url)
        .hash("hash002")
        .build();

    when(this.shortUrlRepositoryOut.findByOriginalUrl(anyString()))
        .thenReturn(Mono.just(shortUrlSaved));

    // Act
    var result = this.findShortUrlUseCase.findByOriginalUrl(url);

    // Assert
    StepVerifier.create(result)
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    verify(this.shortUrlRepositoryOut, times(1)).findByOriginalUrl(anyString());
  }

  @Test
  @DisplayName("testFindByOriginalUrl() -> Error case [not exists]")
  void testFindByOriginalUrlNotFound() {
    // Arrange
    var url = "https://com.google.com.co";

    when(this.shortUrlRepositoryOut.findByOriginalUrl(anyString()))
        .thenReturn(Mono.empty());

    // Act
    var result = this.findShortUrlUseCase.findByOriginalUrl(url);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches((Throwable throwable) ->
            throwable instanceof ObjectNotFoundException exception
                && exception.getKey().equals("SHORT_URL_NOT_FOUND"))
        .verify();

    verify(this.shortUrlRepositoryOut, times(1)).findByOriginalUrl(anyString());
  }

  @Test
  @DisplayName("testFindByHash() -> Good case [found, add interactions, save]")
  void testFindByHash() {
    // Arrange
    var hash = "hash002";
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl("https://com.google.com.co")
        .hash(hash)
        .statistics(ShortUrlStatistics.builder()
            .updatedAt(LocalDateTime.now())
            .creationAt(LocalDateTime.now())
            .amountInteractions(0)
            .build())
        .build();

    when(this.shortUrlRepositoryOut.findByHash(anyString()))
        .thenReturn(Mono.just(shortUrlSaved));
    when(this.shortUrlRepositoryOut.save(any(ShortUrl.class)))
        .thenReturn(Mono.just(shortUrlSaved));

    // Act
    var result = this.findShortUrlUseCase.findByHash(hash);

    // Assert
    StepVerifier.create(result)
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    verify(this.shortUrlRepositoryOut, times(1)).findByHash(anyString());
    verify(this.shortUrlRepositoryOut, times(1)).save(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testFindByHash() -> Error case [not found]")
  void testFindByHashNotFound() {
    // Arrange
    var hash = "hash002";

    when(this.shortUrlRepositoryOut.findByHash(anyString()))
        .thenReturn(Mono.empty());

    // Act
    var result = this.findShortUrlUseCase.findByHash(hash);

    // Assert
    StepVerifier.create(result)
        .expectErrorMatches((Throwable throwable) ->
            throwable instanceof ObjectNotFoundException exception
                && exception.getKey().equals("SHORT_URL_NOT_FOUND"))
        .verify();

    verify(this.shortUrlRepositoryOut, times(1)).findByHash(anyString());
    verify(this.shortUrlRepositoryOut, times(0)).save(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testExistsByHash() -> Good case [exists and not exists]")
  void testExistsByHash() {
    // Arrange
    var hash = "hash002";
    var existsByHashIterations = new LinkedList<>(List.of(Mono.just(Boolean.TRUE),
        Mono.just(Boolean.FALSE)));

    when(this.shortUrlRepositoryOut.existsByHash(anyString()))
        .thenAnswer(invocationOnMock -> existsByHashIterations.poll());

    // Act
    var result1 = this.findShortUrlUseCase.existsByHash(hash);
    var result2 = this.findShortUrlUseCase.existsByHash(hash);

    // Assert
    StepVerifier.create(result1)
        .expectNextMatches((Boolean evaluation1) -> evaluation1.equals(Boolean.TRUE))
        .verifyComplete();
    StepVerifier.create(result2)
        .expectNextMatches((Boolean evaluation2) -> evaluation2.equals(Boolean.FALSE))
        .verifyComplete();

    verify(this.shortUrlRepositoryOut, times(2)).existsByHash(anyString());
  }

}
