package co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.config;

import co.com.mercadolibre.bitly.insfrastructure.adapter.out.persistance.mongodb.entity.ShortUrlEntity;
import java.util.UUID;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MongoDBRepositoryConfig extends ReactiveMongoRepository<ShortUrlEntity, UUID> {

}
