package app.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import app.entities.Order;
import app.exceptions.ProcessTerminatedException;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

	public Mono<Order> verifyOrder(Order order){
		return Mono.create(sink ->{
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
			sink.success(order);
		});
	}
}
