package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.entities.Order;
import app.http.response.GenericResponse;
import app.repositories.ConsumerRepository;
import app.repositories.UserRepository;
import app.services.ConsumerService;
import app.services.TransactionService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired private ConsumerService consumerservice;
	@Autowired private ConsumerRepository consumerrepository;
	@Autowired private UserRepository userrepository;
	@Autowired private TransactionService transactionservice;
	
	@PostMapping("placeOrder")
	public Mono<GenericResponse<Object>> placeOrder(@RequestBody Order order){
		return Mono.create(sink ->{
			//menu list and quantity is available 
			//consumer id is must
			//calculate total price
			//create transaction with r_order id 
			//put transaction in order
			//fill data in order save order with stage placed and status notpaid
			//return order
		});
	}
	
	@GetMapping("preparingOrder/{id}")
	public Mono<GenericResponse<Object>> preparingOrder(@PathVariable String id){
		return Mono.create(sink ->{

		});
	}
	
	@GetMapping("markOrderAsReady/{id}")
	public Mono<GenericResponse<Object>> markOrderAsReady(@PathVariable String id){
		return Mono.create(sink ->{

		});
	}
	
	@GetMapping("markOrderAsServe/{id}")
	public Mono<GenericResponse<Object>> markOrderAsServe(@PathVariable String id){
		return Mono.create(sink ->{
			//order status will be completed
			//stage will be changed
			//order active will be false
		});
	}
}
