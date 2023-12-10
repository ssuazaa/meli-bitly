package co.com.mercadolibre.bitly.insfrastructure.adapter.in.rest.dto;

import lombok.Builder;

@Builder
public record ShortenerRequestDto(String url) {

}
