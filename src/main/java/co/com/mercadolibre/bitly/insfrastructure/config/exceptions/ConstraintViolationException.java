package co.com.mercadolibre.bitly.insfrastructure.config.exceptions;

public class ConstraintViolationException extends BaseException {

  public ConstraintViolationException(String key, String message) {
    super(key, message, 400);
  }

}
