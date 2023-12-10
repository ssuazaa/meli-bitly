package co.com.mercadolibre.bitly.infrastructure.adapter.in.rest.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.LOCATION;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.domain.model.ShortUrlStatistics;
import co.com.mercadolibre.bitly.domain.port.in.CreateShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.adapter.ShortUrlRestController;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerRequestDto;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerResponseDto;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerResponseStatisticsDto;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.mapper.RestShortUrlMapper;
import co.com.mercadolibre.bitly.insfrastructure.config.errorhandler.ErrorResponseDto;
import co.com.mercadolibre.bitly.insfrastructure.config.exceptions.ObjectNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ShortUrlRestController.class})
class ShortUrlRestControllerTest {

  @MockBean
  private RestShortUrlMapper restShortUrlMapper;

  @MockBean
  private CreateShortUrlUseCase createShortUrlUseCase;

  @MockBean
  private FindShortUrlUseCase findShortUrlUseCase;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    when(this.restShortUrlMapper.toResponse(any(ShortUrl.class)))
        .thenAnswer(invocation -> {
          ShortUrl shortUrl = invocation.getArgument(0);
          return ShortenerResponseDto.builder()
              .id(shortUrl.id())
              .originalUrl(shortUrl.originalUrl())
              .hash(shortUrl.hash())
              .statistics(ShortenerResponseStatisticsDto.builder()
                  .creationAt(shortUrl.statistics().creationAt())
                  .updatedAt(shortUrl.statistics().updatedAt())
                  .amountInteractions(shortUrl.statistics().amountInteractions())
                  .build())
              .build();
        });
    when(this.restShortUrlMapper.toResponseWithoutStatistics(any(ShortUrl.class)))
        .thenAnswer(invocation -> {
          ShortUrl shortUrl = invocation.getArgument(0);
          return ShortenerResponseDto.builder()
              .id(shortUrl.id())
              .originalUrl(shortUrl.originalUrl())
              .hash(shortUrl.hash())
              .build();
        });
  }

  @Test
  @DisplayName("testFindAll() -> Good case [not empty]")
  void testFindAll() {
    // Arrange
    var listOfShortUrls = List.of(ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl("https://www.google.com.co")
        .hash("hash002")
        .statistics(ShortUrlStatistics.builder()
            .updatedAt(LocalDateTime.now())
            .creationAt(LocalDateTime.now())
            .amountInteractions(0)
            .build())
        .build(), ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl("https://www.facebook.com")
        .hash("hash003")
        .statistics(ShortUrlStatistics.builder()
            .updatedAt(LocalDateTime.now())
            .creationAt(LocalDateTime.now())
            .amountInteractions(4)
            .build())
        .build());

    when(this.findShortUrlUseCase.findAll()).thenReturn(Flux.fromIterable(listOfShortUrls));

    // Act
    var response = this.webTestClient.get().uri("/api/v1/shortener")
        .header(ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ShortenerResponseDto.class)
        .returnResult()
        .getResponseBody();

    // Assert
    assertNotNull(response);
    assertFalse(response.isEmpty());
    assertAll("Validate each element", () -> {
      for (var i = 0; i < listOfShortUrls.size(); i++) {
        assertEquals(response.get(i).id(), listOfShortUrls.get(i).id());
        assertEquals(response.get(i).originalUrl(), listOfShortUrls.get(i).originalUrl());
        assertEquals(response.get(i).hash(), listOfShortUrls.get(i).hash());
      }
    });

    verify(this.findShortUrlUseCase, times(1)).findAll();
    verify(this.restShortUrlMapper, times(listOfShortUrls.size())).toResponse(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testFindAll() -> Good case [empty]")
  void testFindAllEmpty() {
    // Arrange
    when(this.findShortUrlUseCase.findAll()).thenReturn(Flux.empty());

    // Act
    var response = this.webTestClient.get().uri("/api/v1/shortener")
        .header(ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ShortenerResponseDto.class)
        .returnResult()
        .getResponseBody();

    // Assert
    assertNotNull(response);
    assertTrue(response.isEmpty());

    verify(this.findShortUrlUseCase, times(1)).findAll();
    verify(this.restShortUrlMapper, times(0)).toResponse(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testFindByHash() -> Good case [found]")
  void testFindByHash() {
    // Arrange
    var shortUrlSaved = ShortUrl.builder()
        .id(UUID.randomUUID())
        .originalUrl("https://www.google.com.co")
        .hash("hash002")
        .statistics(ShortUrlStatistics.builder()
            .updatedAt(LocalDateTime.now())
            .creationAt(LocalDateTime.now())
            .amountInteractions(0)
            .build())
        .build();

    when(this.findShortUrlUseCase.findByHash(anyString())).thenReturn(Mono.just(shortUrlSaved));

    // Act
    var response = this.webTestClient.get().uri("/api/v1/shortener/hash/{hash}", "hash002")
        .header(ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBody(ShortenerResponseDto.class)
        .returnResult()
        .getResponseBody();

    // Assert
    assertNotNull(response);
    assertThat(shortUrlSaved.id()).isEqualTo(response.id());
    assertThat(shortUrlSaved.originalUrl()).isEqualTo(response.originalUrl());
    assertThat(shortUrlSaved.hash()).isEqualTo(response.hash());

    verify(this.findShortUrlUseCase, times(1)).findByHash(anyString());
    verify(this.restShortUrlMapper, times(1)).toResponseWithoutStatistics(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testFindByHash() -> Error case [not found]")
  void testFindByHashException() {
    // Arrange
    when(this.findShortUrlUseCase.findByHash(anyString()))
        .thenReturn(Mono.error(() -> new ObjectNotFoundException("SHORT_URL_NOT_FOUND", "")));

    // Act
    var response = this.webTestClient.get().uri("/api/v1/shortener/hash/{hash}", "hash002")
        .header(ACCEPT, "application/json")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(ErrorResponseDto.class)
        .returnResult()
        .getResponseBody();

    // Assert
    assertNotNull(response);
    assertThat(response.key()).isEqualTo("SHORT_URL_NOT_FOUND");

    verify(this.findShortUrlUseCase, times(1)).findByHash(anyString());
    verify(this.restShortUrlMapper, times(0)).toResponseWithoutStatistics(any(ShortUrl.class));
  }

  @Test
  @DisplayName("testCreate() -> Good case [created]")
  void testCreate() {
    // Arrange
    var hashSaved = "hash002";
    var request = ShortenerRequestDto.builder()
        .url("https://www.google.com.co")
        .build();

    when(this.createShortUrlUseCase.create(anyString())).thenReturn(Mono.just(hashSaved));

    // Act
    var response = this.webTestClient.post().uri("/api/v1/shortener")
        .bodyValue(request)
        .header(ACCEPT, "application/json")
        .exchange()
        .expectStatus().isCreated()
        .returnResult(String.class)
        .getResponseHeaders().getFirst(LOCATION);

    // Assert
    assertNotNull(response);
    assertThat(response).isEqualTo("/api/v1/shortener/hash/" + hashSaved);

    verify(this.createShortUrlUseCase, times(1)).create(anyString());
  }

}
