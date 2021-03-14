package app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.entities.Consumer;
import app.entities.Order;
import app.entities.User;
import app.enums.Role;
import app.http.request.ConsumerSignupRequest;
import app.http.response.AuthResponse;
import app.http.response.ConsumerSignupResponse;
import app.http.response.GenericResponse;
import app.repositories.ConsumerRepository;
import app.repositories.UserRepository;
import app.security.JWTUtil;
import app.security.PBKDF2Encoder;
import app.services.ConsumerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController @RequestMapping("consumer")
public class ConsumerController {
	
	 @Autowired private UserRepository userrepo;
	 @Autowired private ConsumerService consumerservice;
	 @Autowired private PBKDF2Encoder passwordEncoder;
	 @Autowired private ConsumerRepository consumerrepo;
	 @Autowired private JWTUtil jwtUtil;


	@PostMapping("signup")
	public Mono<GenericResponse<Object>> signup(@RequestBody ConsumerSignupRequest consumer){
		return Mono.create(sink ->{
			consumerservice.findConsumerByEmailId(consumer.getEmail())
			.switchIfEmpty(Mono.fromRunnable(() ->{
				consumerservice.findConsumerByPhoneNo(consumer.getPhoneNo())
				.switchIfEmpty(Mono.fromRunnable(()->{
					var user = User.builder()
							.email(consumer.getEmail())
							.phoneNo(consumer.getPhoneNo())
							.password(passwordEncoder.encode(consumer.getPassword()))
							.active(true)
							.username(consumer.getPhoneNo())
							.roles(Arrays.asList(Role.CONSUMER))
							.verified(true)
							.build();
					
							userrepo.insert(user)
							.subscribe(data ->{
								var cons = Consumer.builder()
										.firstName(consumer.getFirstName())
										.lastName(consumer.getLastName())
										.orders(new ArrayList())
										.userId(data.getId())
										.build();
								
								consumerrepo.insert(cons)
								.subscribe(c ->{
									consumerservice.transformConsumerToResponse(c).subscribe(co ->{
										sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).message("Sign up successfull")
												.body(GenericResponse.builder().body(ConsumerSignupResponse.builder()
														.consumer(co)
														.roles(data.getRoles())
														.token(jwtUtil.generateToken(data))
														.build()).code(ResponseCode.OK.name()).message("Login successfull").build()).build());
									});
								}, er->{
									sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(er.getMessage().toString()).build());
								});
							}, err->{
								sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage().toString()).build());
							});
				}))
				.subscribe(d ->{
					sink.success(GenericResponse.builder().body(d).code(ResponseCode.WARN.name()).message("Customer is already registered").build());
				}, err->{
					sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage().toString()).build());
				});
			}))
			.subscribe(data ->{
				sink.success(GenericResponse.builder().body(data).code(ResponseCode.WARN.name()).message("Customer is already registered").build());
			}, err->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage().toString()).build());
			});
		});
	}
	
	
	@GetMapping("login/{username}/{password}")
	public Mono<GenericResponse<Object>> login(@PathVariable("username") String username, @PathVariable("password") String password){
		return Mono.create(sink->{
			if(Objects.nonNull(username) && Objects.nonNull(password)) {
				userrepo.findByEmail(username)
				.switchIfEmpty(Mono.fromRunnable( ()->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name()).message("No user account").build());
				}))
				.subscribe(data ->{
					if(data.isActive()) {
						if(data.getPassword().equals(passwordEncoder.encode(password))) {
							data.setPassword(null);
							consumerrepo.findByUserId(data.getId()).subscribe(con ->{
								consumerservice.transformConsumerToResponse(con).subscribe(co ->{
									sink.success(GenericResponse.builder().body(ConsumerSignupResponse.builder()
											.consumer(co)
											.roles(data.getRoles())
											.token(jwtUtil.generateToken(data))
											.build()).code(ResponseCode.OK.name()).message("Login successfull").build());
								});
							}, err->{
								sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
							});
						}else {
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name()).message("Login not successfull").build());

						}
					}else {
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message("User is not active").build());
					}
				}, err->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());

				});
			}else {
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message("Email or password not entered").build());
			}
		});
	}
	
	@GetMapping("getByEmailId/{email}")
	public Mono<GenericResponse<Object>> getByEmailId(@PathVariable("email") String email) {
		return Mono.create(sink ->{
			consumerservice.findConsumerByEmailId(email)
			.switchIfEmpty(Mono.fromRunnable(()->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("No Account found").build());
			}))
			.subscribe(data ->{
				consumerservice.transformConsumerToResponse(data).subscribe(d ->{
					sink.success(GenericResponse.builder().body(d).code(ResponseCode.OK.name()).message("Successfull").build());
				});
			}, err->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage().toString()).build());
			});
		});
	}
	
	@GetMapping("getByPhoneNo/{phone}")
	public Mono<GenericResponse<Object>> getByPhoneNo(@PathVariable("phone") String phone) {
		return Mono.create(sink ->{
			consumerservice.findConsumerByPhoneNo(phone)
			.switchIfEmpty(Mono.fromRunnable(()->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("No Account found").build());
			}))
			.subscribe(data ->{
				consumerservice.transformConsumerToResponse(data).subscribe(d ->{
					sink.success(GenericResponse.builder().body(d).code(ResponseCode.OK.name()).message("Successfull").build());
				});
			}, err->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage().toString()).build());
			});
		});
	}
	
	@GetMapping("getLiveOrders/{id}")
	public Flux<Order> getLiveOrders(@PathVariable String id){
		return Flux.create(sink ->{
			consumerrepo
			.findByOrderActive(id, true)
			.map(c -> c.getOrders())
			.switchIfEmpty(Mono.fromRunnable(() ->{
				sink.complete();
			}))
			.subscribe(d ->{
				if(d.size()>0) {
					Flux.fromIterable(d)
					.sort(Comparator.comparing(Order::getCreatedOn))
					.doOnNext(r ->{
						sink.next(r);
					})
					.doOnComplete( () ->{
						sink.complete();
					});
				}else {
					sink.complete();
				}
			}, err->{
				sink.error(err);
				sink.complete();
			});
		});
	}
}
