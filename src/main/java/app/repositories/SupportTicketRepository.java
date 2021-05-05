package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.SupportTicket;
import reactor.core.publisher.Flux;

public interface SupportTicketRepository extends ReactiveMongoRepository<SupportTicket, String>{

	@Query("{'requester.id': ?0}")
	public Flux<SupportTicket> findByRequestedUserId(String id);
	
	@Query("{'reviewver.id': ?0}")
	public Flux<SupportTicket> findByReviewerUserId(String id);
	
	@Query("{'requester.id': ?0, 'status': ?1}")
	public Flux<SupportTicket> findByRequestedUserIdAndStatus(String id, String status);
	
	@Query("{'status': ?0}")
	public Flux<SupportTicket> findByStatus(String status);

}
