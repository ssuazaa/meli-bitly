package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto.ShortenerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface RestShortUrlMapper {

  ShortenerResponseDto toResponse(ShortUrl shortUrl);

  @Mapping(target = "statistics", ignore = true)
  ShortenerResponseDto toResponseWithoutStatistics(ShortUrl shortUrl);

}

