package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Constants;
import app.entities.Hotel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConstantsRepository  extends ReactiveMongoRepository<Constants, String>{

	@Query("{'key':?0}")
	public Mono<Constants> findByKey(String key);
	
}
