package co.com.mercadolibre.bitly.insfrastructure.config.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

  private final String key;
  private final Integer statusCode;

  public BaseException(String key, String message, int statusCode) {
    super(message);
    this.key = key;
    this.statusCode = statusCode;
  }

}
