package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Hotel;
import reactor.core.publisher.Mono;

public interface HotelRepository extends ReactiveMongoRepository<Hotel, String>{
	
	@Query("{'user.username': ?0}")
	public Mono<Hotel> findByUsername(String username);
	
	
}
