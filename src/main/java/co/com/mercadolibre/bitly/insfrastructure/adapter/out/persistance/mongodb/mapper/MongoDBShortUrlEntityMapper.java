package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import co.com.mercadolibre.bitly.domain.model.ShortUrl;
import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.entity.ShortUrlEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface MongoDBShortUrlEntityMapper {

  ShortUrl toDomain(ShortUrlEntity shortUrlEntity);

  @InheritInverseConfiguration
  ShortUrlEntity toEntity(ShortUrl shortUrl);

}

