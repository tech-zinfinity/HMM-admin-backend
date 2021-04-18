package app.controllers;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;

import app.constants.OrderStages;
import app.constants.OrderStatus;
import app.constants.ResponseCode;
import app.constants.TransactionType;
import app.entities.Order;
import app.exceptions.ProcessTerminatedException;
import app.http.response.GenericResponse;
import app.repositories.ConsumerRepository;
import app.repositories.OrderRepository;
import app.repositories.UserRepository;
import app.services.ConsumerService;
import app.services.OrderService;
import app.services.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired private ConsumerService consumerservice;
	@Autowired private ConsumerRepository consumerrepository;
	@Autowired private UserRepository userrepository;
	@Autowired private TransactionService transactionservice;
	@Autowired private OrderService orderService;
	@Autowired private OrderRepository orderrepo;
	
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
			orderService.verifyOrder(order).subscribe(od ->{
				double[] prices =  {0.0};
				od.getItems().forEach(s -> {
					prices[0] = prices[0]+s.getPrice();
				});
				try {
					transactionservice.startPayment(TransactionType.INBOUND, "SYSTEM", order.getTransaction().getPaidByPhone(),
							order.getTransaction().getPaidByEmail(), "INR", prices[0]*100.00)
					.subscribe(tr ->{
						od.setTransaction(tr);
						od.setActive(true);
						od.setStage(OrderStages.DRAFTED);
						od.setStatus(OrderStatus.DRAFT);
						od.setTotalPrice(prices[0]);
						sink.success(GenericResponse.builder().body(od).message("Order created successfully").code(ResponseCode.OK.name()).build());
					});
				} catch (RazorpayException e) {
					e.printStackTrace();
					sink.success(GenericResponse.builder().message(e.getMessage()).code(ResponseCode.ERR.name()).build());
				}
			}, err->{
				if(err instanceof ProcessTerminatedException) {
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());
				}
			});
			
		});
	}
	
	@PostMapping("captureOrder")
	public Mono<GenericResponse<Object>> captureOrder(@RequestBody Order order){
		return Mono.create(sink ->{
			orderService.verifyOrderForConfirmation(order).subscribe(od ->{
				orderService.captureOrder(od).subscribe(o ->{
					sink.success(GenericResponse.builder().message("Order placed successfully").code(ResponseCode.OK.name()).body(o).build());
				}, err->{
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());

				});
			}, err->{
				if(err instanceof ProcessTerminatedException) {
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());
				}
			});
			
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
			//order active will be false4
		});
	}
	
	@GetMapping("getByUserId/{id}")
	public Flux<Order> getByUserId(@PathVariable("id") String id){
		return orderrepo.findByCustId(id);
	}
}
