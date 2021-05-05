package app.dboperations;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

public class GenericEntityRepository <T> {
	
	final Class<T> ClassType;
	
	private List<GenericEntityRepository<T>> instances;
	
	public GenericEntityRepository(Class<T> ClassType) {
		this.ClassType = ClassType;
	}
	
	public GenericEntityRepository<T> getInstance(Class<T> ClassType){
		if(Objects.nonNull(this.instances)) {
			if(this.instances.stream().filter(d -> d.ClassType.equals(ClassType)).count()>0) {
				return this.instances.stream().filter(d -> d.ClassType.equals(ClassType)).findFirst().get();
			}else {
				var x  = new GenericEntityRepository<>(ClassType);
				this.updateInstances(x);
				return x;
			}
		}else {
			var x  = new GenericEntityRepository<>(ClassType);
			this.updateInstances(x);
			return x;		
		}
	}
	
	private void updateInstances(GenericEntityRepository c) {
		if(Objects.nonNull(this.instances)) {
			this.instances.add(c);
		}
	}

	@Autowired private ReactiveMongoOperations operations;
	
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

}
