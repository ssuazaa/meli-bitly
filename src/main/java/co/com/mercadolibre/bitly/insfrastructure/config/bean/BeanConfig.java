package co.com.mercadolibre.bitly.insfrastructure.config.bean;

import co.com.mercadolibre.bitly.application.usecases.CreateShortUrlUseCaseImpl;
import co.com.mercadolibre.bitly.application.usecases.FindShortUrlUseCaseImpl;
import co.com.mercadolibre.bitly.domain.port.in.CreateShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.in.FindShortUrlUseCase;
import co.com.mercadolibre.bitly.domain.port.out.ShortUrlRepositoryOut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  private final ShortUrlRepositoryOut shortUrlRepositoryOut;

  public BeanConfig(ShortUrlRepositoryOut shortUrlRepositoryOut) {
    this.shortUrlRepositoryOut = shortUrlRepositoryOut;
  }

  @Bean
  public FindShortUrlUseCase findShortUrlUseCase() {
    return new FindShortUrlUseCaseImpl(this.shortUrlRepositoryOut);
  }

  @Bean
  public CreateShortUrlUseCase createShortUrlUseCase() {
    return new CreateShortUrlUseCaseImpl(findShortUrlUseCase(), this.shortUrlRepositoryOut);
  }

}
