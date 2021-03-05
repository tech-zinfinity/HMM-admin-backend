package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Customer;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String>{

	@Query("{'userId':?0}")
	public Mono<Customer> findByUserId(String userId);
	
	@Query("{'phoneNo':?0}")
	public Mono<Customer> findByPhoneNo(String phoneNo);
}
