package co.com.mercadolibre.bitly.insfrastructure.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortenAlgorithm {

  private static final SecureRandom random = new SecureRandom();

  private ShortenAlgorithm() {

  }

  public static String generateHash(String url) {
    try {
      var md = MessageDigest.getInstance("SHA-256");
      md.update(url.getBytes());
      var digest = md.digest();
      var hashStr = new StringBuilder();
      for (var b : digest) {
        hashStr.append(String.format("%02x", b));
      }
      return hashStr.substring(0, random.nextInt(18));
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

}
