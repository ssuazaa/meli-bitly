package co.com.mercadolibre.bitly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MeliBitlyApplication {

  public static void main(String[] args) {
    SpringApplication.run(MeliBitlyApplication.class, args);
  }

}
