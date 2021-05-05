package app.controllers;


import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.razorpay.RazorpayException;

import app.constants.OrderStages;
import app.constants.OrderStatus;
import app.constants.ResponseCode;
import app.constants.TransactionType;
import app.entities.Order;
import app.exceptions.ProcessTerminatedException;
import app.http.response.GenericResponse;
import app.models.HotelOrderModel;
import app.repositories.HotelRepository;
import app.repositories.OrderRepository;
import app.repositories.TransactionRepository;
import app.services.OrderService;
import app.services.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired private TransactionService transactionservice;
	@Autowired private OrderService orderService;
	@Autowired private TransactionRepository transactionrepo;
	@Autowired private OrderRepository orderrepo;
	@Autowired private HotelRepository hotelrepo;
	private Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	@PostMapping("placeOrder")
	public Mono<GenericResponse<Object>> placeOrder(@RequestBody Order order){
		return Mono.create(sink ->{
			orderService.verifyOrder(order).subscribe(od ->{
				double[] prices =  {0.0};
				od.getItems().forEach(s -> {
					prices[0] = prices[0]+s.getPrice();
				});
				this.hotelrepo.findById(od.getHotelId())
				.switchIfEmpty(Mono.fromRunnable( () ->{
					sink.success(GenericResponse.builder().message("No Hotel associated with hotel id, please check again").code(ResponseCode.ERR.name()).build());
				}))
				.subscribe(hotel ->{
					od.setHotel(mapper.map(hotel, HotelOrderModel.class));
					od.setTableName(hotel.getTables().stream().filter(d->d.getTableId().equals(od.getTableId())).findFirst().get().getTableNo());
					try {
						transactionservice.startPayment(TransactionType.INBOUND, "SYSTEM", order.getTransaction().getPaidByPhone(),
								order.getTransaction().getPaidByEmail(), "INR", prices[0]*100.00)
						.subscribe(tr ->{
							tr.setGst(hotel.getGstNo());
							tr.setPan(hotel.getPanNo());
							tr.setHotelId(hotel.getId());
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
				},err->{
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());
				});
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
					err.printStackTrace();
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());

				});
			}, err->{
				err.printStackTrace();
				if(err instanceof ProcessTerminatedException) {
					sink.success(GenericResponse.builder().message(err.getMessage()).code(ResponseCode.ERR.name()).build());
				}
			});
			
		});
	}
	
	@GetMapping("/revert/{id}")
	public Mono<GenericResponse<Object>> revertOrder(@PathVariable("id") String id){
		return Mono.create(sink ->{
			orderrepo.findById(id).switchIfEmpty(Mono.fromRunnable( () ->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("No order available").build());
			})).subscribe(d ->{
				if(d.getStatus()!= OrderStatus.PAID && d.getStage()==OrderStages.DRAFTED) {
					if(Objects.nonNull(d.getTransaction())) {
						transactionrepo.findById(d.getTransaction().getId()).switchIfEmpty(Mono.fromRunnable( () ->{
							orderrepo.delete(d).subscribe(o ->{
								
							}, err->{
								sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Problem in reverting oder").build());
							}, () ->{
								sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("Order with payment reverted successfully").build());
							});
						})).subscribe(tr ->{
							if(!(tr.isSuccess())) {
								transactionrepo.delete(tr).subscribe(t ->{
									
								}, err->{
									sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Problem in reverting transaction").build());
								}, ()->{
									orderrepo.delete(d).subscribe(o ->{
										
									}, err->{
										sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Problem in reverting oder").build());
									}, () ->{
										sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("Order with payment reverted successfully").build());
									});
								});
							}else {
								sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Transaction is already processed, cannot be reverted back").build());
							}

						}, err->{
							
						});
					}else {
						orderrepo.delete(d).subscribe(o ->{
							
						}, err->{
							sink.success(GenericResponse.builder().code(ResponseCode.ERR.toString()).message("Problem in reverting oder").build());
						}, () ->{
							sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("Order with payment reverted successfully").build());
						});
					}
				}else {
					sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Order is already placed or paid, cannot be reverted").build());
				}

			},err->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.toString()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("/revert/transaction/{id}")
	public Mono<GenericResponse<Object>> revertTransaction(@PathVariable("id") String id){
		return Mono.create(sink ->{
			transactionrepo.findById(id).switchIfEmpty(Mono.fromRunnable( () ->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("No Transaction available").build());

			})).subscribe(tr ->{
				if(!(tr.isSuccess())) {
					transactionrepo.delete(tr).subscribe(t ->{
						
					}, err->{
						sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Problem in reverting transaction").build());
					}, ()->{
						sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("Transaction reverted successfully").build());
					});

				}else {
					sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Problem in reverting transaction").build());
				}
			}, err->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message(err.getMessage()).build());
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
		System.out.println(id);
		return orderrepo.findByCustId(id);
	}
}
