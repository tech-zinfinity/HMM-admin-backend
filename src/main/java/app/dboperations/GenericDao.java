package app.dboperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;

public interface GenericDao {

	static List<GenericEntityRepository<?>> instances = new ArrayList<GenericEntityRepository<?>>();

	public static GenericEntityRepository<?> getInstance(Class<?> ClassType, ReactiveMongoOperations operations){
		if(Objects.nonNull(instances)) {
			if(instances.stream().filter(d -> d.ClassType.equals(ClassType)).count()>0) {
				return (GenericEntityRepository<?>) instances.stream().filter(d -> d.ClassType.equals(ClassType)).findFirst().get();
			}else {
				var x  = new GenericEntityRepository<>(ClassType, operations);
				updateInstances(x);
				return x;
			}
		}else {
			var x  = new GenericEntityRepository<>(ClassType, operations);
			updateInstances(x);
			return x;		
		}
	}
	
	private static void updateInstances(GenericEntityRepository<?> c) {
		if(Objects.nonNull(instances)) {
			instances.add(c);
		}
	}
}
