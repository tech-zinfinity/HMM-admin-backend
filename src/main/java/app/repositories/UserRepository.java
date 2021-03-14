package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String>{

	@Query("{'username':?0}")
	public Mono<User> findByUsername(String username);
	
	@Query("{'email':?0}")
	public Mono<User> findByEmail(String email);
	
	@Query("{'phoneNo':?0}")
	public Mono<User> findByPhoneNo(String phoneNo);
}
