package co.com.mercadolibre.bitly.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import co.com.mercadolibre.bitly.insfrastructure.util.ShortenAlgorithm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShortenAlgorithmTest {

  @Test
  @DisplayName("testGenerateHash() -> Good case")
  void testGenerateHash() {
    // Arrange
    var url = "https://www.google.com.co";

    // Act
    var hash = ShortenAlgorithm.generateHash(url);

    // Assert
    assertNotNull(hash);
  }

  @Test
  @DisplayName("testGenerateHash() -> Error case")
  void testGenerateHashError() {
    // Arrange
    var url = "https://www.google.com.co";

    // Act && Assert
    try (var messageDigestMock = mockStatic(MessageDigest.class)) {
      messageDigestMock.when(() -> MessageDigest.getInstance(anyString()))
          .thenThrow(new NoSuchAlgorithmException());

      assertThat(ShortenAlgorithm.generateHash(url)).isEmpty();
    }
  }

}
