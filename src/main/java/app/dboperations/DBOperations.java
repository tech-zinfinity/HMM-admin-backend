package app.dboperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import app.entities.Hotel;
import app.enums.HotelStatus;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class DBOperations {

	@Autowired private ReactiveMongoOperations operations;
	
	public Flux<Hotel> getLatestRequestedHotelsByLimit(int limit) {
		try {
			Query query = new Query(Criteria.where("status").is(HotelStatus.REQUESTED.name()));
			   query.addCriteria(Criteria.where("active").is(false));
			   query.limit(limit);
			   query.with(Sort.by(Sort.Direction.DESC, "publishedOn"));
			   return operations.find(query, Hotel.class);
		}catch (Exception e) {
			log.info("Fatal", e.getMessage());
			return null;
		}
	}
	
	public Flux<Hotel> getLatestApprovedHotelsByLimit(int limit) {
		try {
			Query query = new Query(Criteria.where("status").is(HotelStatus.APPROVED.name()));
			   query.addCriteria(Criteria.where("active").is(false));
			   query.limit(limit);
			   query.with(Sort.by(Sort.Direction.DESC, "publishedOn"));
			   return operations.find(query, Hotel.class);
		}catch (Exception e) {
			log.info("Fatal", e.getMessage());
			return null;
		}
	}
	
	public Flux<Hotel> getLatestRejectedHotelsByLimit(int limit) {
		try {
			Query query = new Query(Criteria.where("status").is(HotelStatus.REJECTED.name()));
			   query.addCriteria(Criteria.where("active").is(false));
			   query.limit(limit);
			   query.with(Sort.by(Sort.Direction.DESC, "publishedOn"));
			   return operations.find(query, Hotel.class);
		}catch (Exception e) {
			log.info("Fatal", e.getMessage());
			return null;
		}
	}
	
	public Flux<Hotel> getLatestPublishedHotelByLimit(int limit) {
		try {
			Query query = new Query(Criteria.where("status").is(HotelStatus.PUBLISHED.name()));
			   query.addCriteria(Criteria.where("active").is(true));
			   query.limit(limit);
			   query.with(Sort.by(Sort.Direction.DESC, "publishedOn"));
			   return operations.find(query, Hotel.class);
		}catch (Exception e) {
			log.info("Fatal", e.getMessage());
			return null;
		}
	}
	
	public Flux<Hotel> getLatestVerifiedHotelByLimit(int limit) {
		try {
			Query query = new Query(Criteria.where("status").is(HotelStatus.VERIFIED.name()));
			   query.addCriteria(Criteria.where("active").is(false));
			   query.limit(limit);
			   query.with(Sort.by(Sort.Direction.DESC, "publishedOn"));
			   return operations.find(query, Hotel.class);
		}catch (Exception e) {
			log.info("Fatal", e.getMessage());
			return null;
		}
	}
	
}
