package app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entities.Consumer;
import app.enums.Role;
import app.exceptions.EntityNotFoundException;
import app.http.response.ConsumerResponse;
import app.repositories.ConsumerRepository;
import app.repositories.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class ConsumerService {

	
	@Autowired private UserRepository userrepo;
	@Autowired private ConsumerRepository consumerrepo;
	
	
	public Mono<Consumer> findConsumerByEmailId(String email) {
		return Mono.create(sink ->{
			userrepo.findByEmail(email)
			.switchIfEmpty(Mono.fromRunnable( () ->{
				sink.success();
			}))
			.subscribe(data ->{
				if(data.getRoles().contains(Role.CONSUMER)) {
					this.consumerrepo.findByUserId(data.getId())
					.switchIfEmpty(Mono.fromRunnable( ()->{
						sink.error(new EntityNotFoundException());
					}))
					.subscribe(consumer ->{
						sink.success(consumer);
					}, err->{
						sink.error(err);
					});
				}else {
					sink.success();
				}
			}, err->{
				sink.error(err);
			});
		});
	}
	
	public Mono<Consumer> findConsumerByPhoneNo(String phone) {
		return Mono.create(sink ->{
			userrepo.findByPhoneNo(phone)
			.switchIfEmpty(Mono.fromRunnable( () ->{
				sink.success();
			}))
			.subscribe(data ->{
				if(data.getRoles().contains(Role.CONSUMER)) {
					this.consumerrepo.findByUserId(data.getId())
					.switchIfEmpty(Mono.fromRunnable( ()->{
						sink.error(new EntityNotFoundException());
					}))
					.subscribe(consumer ->{
						sink.success(consumer);
					}, err->{
						sink.error(err);
					});
				}else {
					sink.success();
				}
			}, err->{
				sink.error(err);
			});
		});
	}
	
	public Mono<ConsumerResponse> transformConsumerToResponse(Consumer consumer){
		return Mono.create(sink ->{
			userrepo.findById(consumer.getUserId())
			.switchIfEmpty(Mono.fromRunnable(() ->{
				sink.success();
			}))
			.subscribe(data ->{
				var c = ConsumerResponse.builder()
						.firstName(consumer.getFirstName())
						.lastName(consumer.getLastName())
						.orders(consumer.getOrders())
						.id(consumer.getId())
						.user(data)
						.build();
				
				sink.success(c);
			}, err->{
				sink.error(err);
			});
		});
	}
	
	public Mono<ConsumerResponse> transformConsumerToResponse(String consumerId){
		return Mono.create(sink ->{
			consumerrepo.findById(consumerId)
			.switchIfEmpty(Mono.fromRunnable(() ->{
				sink.success();
			}))
			.subscribe(data ->{
				this.transformConsumerToResponse(data)
				.switchIfEmpty(Mono.fromRunnable(() ->{
					sink.success();
				}))
				.subscribe(con ->{
					sink.success(con);
				}, err->{
					sink.error(err);
				});
			}, err->{
				sink.error(err);
			});
		});
	}
	
	public Mono<ConsumerResponse> transformConsumerToResponseByUserId(String userId){
		return Mono.create(sink ->{
			consumerrepo.findByUserId(userId)
			.switchIfEmpty(Mono.fromRunnable(() ->{
				sink.success();
			}))
			.subscribe(data->{
				this.transformConsumerToResponse(data)
				.switchIfEmpty(Mono.fromRunnable(() ->{
					sink.success();
				}))
				.subscribe(d ->{
					sink.success(d);
				}, er->{
					sink.error(er);
				});
			}, err->{
				sink.error(err);
			});
		});
	}
}
