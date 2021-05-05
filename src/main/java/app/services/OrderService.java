package app.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.constants.OrderStages;
import app.constants.OrderStatus;
import app.entities.Order;
import app.exceptions.ProcessTerminatedException;
import app.repositories.OrderRepository;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

	@Autowired TransactionService transactionservice;
	@Autowired OrderRepository orderrepo;
	
	public Mono<Order> verifyOrder(Order order){
		return Mono.create(sink ->{
			if(!Objects.nonNull(order.getTransaction().getPaidByEmail())) {
				sink.error(new ProcessTerminatedException("No Email available"));
			}
			if(!Objects.nonNull(order.getTransaction().getPaidByPhone())) {
				sink.error(new ProcessTerminatedException("No Phone available"));
			}
			if(!Objects.nonNull(order.getCustId())) {
				sink.error(new ProcessTerminatedException("No Customer Id available"));
			}
			if(!Objects.nonNull(order.getItems())) {
				sink.error(new ProcessTerminatedException("No Items available"));
			}
			if(!Objects.nonNull(order.getHotelId())) {
				sink.error(new ProcessTerminatedException("Hotel id is not provided"));
			}
			if(!Objects.nonNull(order.getTableId())) {
				sink.error(new ProcessTerminatedException("Table id is not provided"));
			}
			else {
				if(order.getItems().size() == 0) {
					sink.error(new ProcessTerminatedException("No Items available"));
				}
			}
			sink.success(order);
		});
	}
	
	public Mono<Order> verifyOrderForConfirmation(Order order){
		return Mono.create(sink ->{
			if(!Objects.nonNull(order.getTransaction().getPaidByEmail())) {
				sink.error(new ProcessTerminatedException("No Email available"));
			}
			if(!Objects.nonNull(order.getTransaction().getPaidByPhone())) {
				sink.error(new ProcessTerminatedException("No Phone available"));
			}
			if(!Objects.nonNull(order.getCustId())) {
				sink.error(new ProcessTerminatedException("No Customer Id available"));
			}
			if(!Objects.nonNull(order.getItems())) {
				sink.error(new ProcessTerminatedException("No Items available"));
			}else {
				if(order.getItems().size() == 0) {
					sink.error(new ProcessTerminatedException("No Items available"));
				}
			}
			if(!Objects.nonNull(order.getTransaction().getRazorPaySignature())) {
				sink.error(new ProcessTerminatedException("No Signature available"));
			}
			if(!Objects.nonNull(order.getTransaction().getROrderId())) {
				sink.error(new ProcessTerminatedException("No getROrderId available"));
			}
			if(!Objects.nonNull(order.getTransaction().getRazorPayKey())) {
				sink.error(new ProcessTerminatedException("No getRazorPayKey available"));
			}
			if(!Objects.nonNull(order.getTransaction().getRazorPayPaymentId())) {
				sink.error(new ProcessTerminatedException("No RazorPayPaymentId available"));
			}
			sink.success(order);
		});
	}
	
	public Mono<Order> captureOrder(Order order) {
		return Mono.create(sink ->{
			transactionservice.capturePayment(order.getTransaction()).subscribe(tr ->{
				order.setTransaction(tr);
				order.setStage(OrderStages.PLACED);
				order.setStatus(OrderStatus.PAID);
				order.setActive(true);
				orderrepo.save(order).subscribe(od ->{
					sink.success(od);
				}, err->{
					sink.error(err);
				});
			}, err->{
				sink.error(err);
			});
		});
	}
	

}
