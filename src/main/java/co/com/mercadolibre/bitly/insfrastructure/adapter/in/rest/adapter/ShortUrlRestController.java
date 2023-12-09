package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.adapter;

import co.com.mercadolibre.bitly.domain.port.in.CreateShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerRequestDto;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerResponseDto;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.mapper.RestShortUrlMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/v1/shortener")
public class ShortUrlRestController {

  private final RestShortUrlMapper mapper;
  private final CreateShortUrlUseCase createShortUrlUseCase;
  private final FindShortUrlUseCase findShortUrlUseCase;

  public ShortUrlRestController(RestShortUrlMapper mapper,
      CreateShortUrlUseCase createShortUrlUseCase, FindShortUrlUseCase findShortUrlUseCase) {
    this.mapper = mapper;
    this.createShortUrlUseCase = createShortUrlUseCase;
    this.findShortUrlUseCase = findShortUrlUseCase;
  }

  @GetMapping
  public Flux<ShortenerResponseDto> findAll() {
    return this.findShortUrlUseCase.findAll()
        .map(this.mapper::toResponse);
  }

  @GetMapping("/hash/{hash}")
  public Mono<ResponseEntity<ShortenerResponseDto>> findByShortUrl(@PathVariable String hash) {
    return this.findShortUrlUseCase.findByHash(hash)
        .map(this.mapper::toResponseWithoutStatistics)
        .map(ResponseEntity::ok);
  }

  @PostMapping
  public Mono<ResponseEntity<Void>> create(
      @RequestBody @Valid ShortenerRequestDto shortenerRequestDto,
      UriComponentsBuilder uriBuilder) {
    return this.createShortUrlUseCase.create(shortenerRequestDto.url())
        .map((String hash) -> ResponseEntity.created(uriBuilder
                .path("/api/v1/shortener/hash/{hash}")
                .buildAndExpand(hash)
                .toUri())
            .build());
  }

}
