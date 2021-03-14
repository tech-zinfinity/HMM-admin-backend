package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConsumerRepository extends ReactiveMongoRepository<Consumer, String>{
	
	public Mono<Consumer> findByUserId(String userId);
	
	@Query("{'id': ?0, 'orders': {'active' : ?1}}")
	public Mono<Consumer> findByOrderActive(String id, boolean active);
}
