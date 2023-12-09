package co.com.mercadolibre.bitly.application.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.port.in.CreateShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import co.com.mercadolibre.bitly.insfrastructure.config.exceptions.ConstraintViolationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreateShortUrlUseCaseImplTest {

  @MockBean
  FindShortUrlUseCase findShortUrlUseCase;

  @MockBean
  ShortUrlRepositoryOut shortUrlRepositoryOut;

  CreateShortUrlUseCase createShortUrlUseCase;

  @BeforeEach
  public void setUp() {
    this.findShortUrlUseCase = mock(FindShortUrlUseCase.class);
    this.shortUrlRepositoryOut = mock(ShortUrlRepositoryOut.class);
    this.createShortUrlUseCase = new CreateShortUrlUseCaseImpl(this.findShortUrlUseCase,
        this.shortUrlRepositoryOut);
  }

  @Test
  @DisplayName("testCreate() -> Good case [exists by originalUrl, not create new ShortUrl]")
  void testCreateAlreadyExists() {
    // Arrange
    var url = "https://com.google.com.co";
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl(url)
        .hash("hash002")
        .build();

    when(this.findShortUrlUseCase.findByOriginalUrl(anyString()))
        .thenReturn(Mono.just(shortUrlSaved));

    // Act
    var result = this.createShortUrlUseCase.create(url);

    // Assert
    StepVerifier.create(result)
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    verify(this.findShortUrlUseCase, times(1)).findByOriginalUrl(anyString());
    verify(this.findShortUrlUseCase, times(0)).existsByHash(anyString());
    verify(this.shortUrlRepositoryOut, times(0)).save(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testCreate() -> Good case [not exists by originalUrl, generate hash, create new ShortUrl]")
  void testCreate() {
    // Arrange
    var url = "https://com.google.com.co";
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl(url)
        .hash("hash002")
        .build();

    when(this.findShortUrlUseCase.findByOriginalUrl(anyString()))
        .thenReturn(Mono.error(new ConstraintViolationException("SHORT_URL_NOT_FOUND", "")));
    when(this.findShortUrlUseCase.existsByHash(anyString()))
        .thenReturn(Mono.just(Boolean.FALSE));
    when(this.shortUrlRepositoryOut.save(any(ShortUrl.class)))
        .thenReturn(Mono.just(shortUrlSaved));

    // Act
    var result = this.createShortUrlUseCase.create(url);

    // Assert
    StepVerifier.create(result)
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    verify(this.findShortUrlUseCase, times(1)).findByOriginalUrl(anyString());
    verify(this.findShortUrlUseCase, times(1)).existsByHash(anyString());
    verify(this.shortUrlRepositoryOut, times(1)).save(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testCreate() -> Good case [not exists by originalUrl, generate 3 times the hash, create new ShortUrl]")
  void testCreateHashExists() {
    // Arrange
    var url = "https://com.google.com.co";
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl(url)
        .hash("hash002")
        .build();
    var existsByHashIterations = new LinkedList<>(List.of(Mono.just(Boolean.TRUE),
        Mono.just(Boolean.TRUE),
        Mono.just(Boolean.FALSE)));

    when(this.findShortUrlUseCase.findByOriginalUrl(anyString()))
        .thenReturn(Mono.error(new ConstraintViolationException("SHORT_URL_NOT_FOUND", "")));
    when(this.findShortUrlUseCase.existsByHash(anyString()))
        .thenAnswer(invocationOnMock -> existsByHashIterations.poll());
    when(this.shortUrlRepositoryOut.save(any(ShortUrl.class)))
        .thenReturn(Mono.just(shortUrlSaved));

    // Act
    var result = this.createShortUrlUseCase.create(url);

    // Assert
    StepVerifier.create(result)
        .expectNextCount(1)
        .expectComplete()
        .verify();

    verify(this.findShortUrlUseCase, times(1)).findByOriginalUrl(anyString());
    verify(this.findShortUrlUseCase, times(3)).existsByHash(anyString());
    verify(this.shortUrlRepositoryOut, times(1)).save(any(ShortUrl.class));
  }

}
