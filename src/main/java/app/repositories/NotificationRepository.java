package app.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Notification;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String>{

	@Query(value = "{ 'userIds' : {$all : [?0] }}")
	public Flux<Notification> findByUserIds(String userId);
}
