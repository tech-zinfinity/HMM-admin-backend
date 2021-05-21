package app.dboperations;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GenericEntityRepository <T> {
	
	final Class<T> ClassType;
		
	public GenericEntityRepository(Class<T> ClassType, ReactiveMongoOperations operations) {
		this.ClassType = ClassType;
		this.operations = operations;
	}

	private ReactiveMongoOperations operations;
	
	/* findById
	 * findByIds
	 * save
	 * update
	 * delete
	 * getPage
	 * */
	
	public Mono<T> findById(Object id) {
		return Mono.create(sink ->{
			sink.success(operations.find(new Query(Criteria.where("id").is(id)), ClassType).toStream().findFirst().get());
		});
	}
	
	public Flux<T> findAll(){
		return operations.findAll(ClassType);
	}

}
