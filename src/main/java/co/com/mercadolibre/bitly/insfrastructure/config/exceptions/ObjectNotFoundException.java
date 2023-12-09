package co.com.mercadolibre.bitly.insfrastructure.config.exceptions;

public class ObjectNotFoundException extends BaseException {

  public ObjectNotFoundException(String key, String message) {
    super(key, message, 404);
  }

}
