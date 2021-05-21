package app.dboperations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import app.exceptions.ProcessTerminatedException;
import app.models.FieldConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GenericEntityService {

	@Autowired ReactiveMongoOperations operations;
	
	private static Class<?> getClassInstance(String name) throws ClassNotFoundException {
		return Class.forName("app.entities."+name);
	}
	
	public Flux<?> findAll(Class<?> type){
		return GenericDao.getInstance(type, operations).findAll();
	}
	
	public Flux<?> findAll(String tableName) throws ClassNotFoundException{
		return GenericDao.getInstance(getClassInstance(tableName), operations).findAll();
	}
	
	public Mono<?> findById(Class<?> type, Object id){
		return GenericDao.getInstance(type, operations).findById(id);
	}
	
	public Mono<?> findById(String tableName, Object id) throws ClassNotFoundException{
		return GenericDao.getInstance(getClassInstance(tableName), operations).findById(id);
	}
	
	public Flux<?> findByField(Class<?> type, List<FieldConfiguration<?>> values){
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			return operations.find(query, type);
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	} 
	
	public Flux<?> findByFieldAndSort(Class<?> type, List<FieldConfiguration<?>> values, String sortColumn, Sort.Direction direction){
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			query.with(Sort.by(direction, sortColumn));
			return operations.find(query, type);
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
	public Flux<?> findByFieldAndSortAndLimit(Class<?> type, List<FieldConfiguration<?>> values, String sortColumn, Sort.Direction direction, int limit){
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			query.limit(limit);
			query.with(Sort.by(direction, sortColumn));
			return operations.find(query, type);
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
	public Flux<?> findByField(String type, List<FieldConfiguration<?>> values) throws ClassNotFoundException{
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			return operations.find(query, getClassInstance(type));
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	} 
	
	public Flux<?> findByFieldAndSort(String type, List<FieldConfiguration<?>> values, String sortColumn, Sort.Direction direction) throws ClassNotFoundException{
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			query.with(Sort.by(direction, sortColumn));
			return operations.find(query, getClassInstance(type));
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
	public Flux<?> findByFieldAndSortAndLimit(String type, List<FieldConfiguration<?>> values, String sortColumn, Sort.Direction direction, int limit) throws ClassNotFoundException{
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			query.limit(limit);
			query.with(Sort.by(direction, sortColumn));
			return operations.find(query, getClassInstance(type));
		}else {
			return Flux.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
	public Mono<Long> getCount(String type, List<FieldConfiguration<?>> values) throws ClassNotFoundException{
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			return operations.count(query, getClassInstance(type));
		}else {
			return Mono.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
	public Mono<Long> getCount(Class<?> type, List<FieldConfiguration<?>> values) throws ClassNotFoundException{
		if(values.size()>0) {
			Query query  = new Query();
			values.forEach(value ->{
				query.addCriteria(Criteria.where(value.getName()).is(value.getValue()));
			});
			return operations.count(query, type);
		}else {
			return Mono.error(new ProcessTerminatedException("List is empty"));
		}
	}
	
}
