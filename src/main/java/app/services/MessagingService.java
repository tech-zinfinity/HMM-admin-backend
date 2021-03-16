package app.services;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import app.models.MessagingRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MessagingService {

	@Value("${message.api.host}")
	private String apihost;
	
	@Value("${message.api.endpoint.sendmessage}")
	private String sendmessageendpoint;
	
	public Mono<Boolean> sendMessage(String number, String message){
		return Mono.create(sink ->{
			var body = MessagingRequest.builder()
					.numbers(Arrays.asList(number))
					.message(message)
					.build();
			WebClient webClient = WebClient.create(apihost);
			
			Flux<String> response = webClient.post()
			.uri(sendmessageendpoint)
			.body(Mono.just(body), MessagingRequest.class)
			.retrieve()
			.bodyToFlux(String.class);
			
			response
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(false);
			}))
			.subscribe(data->{
				if (Objects.nonNull(data)) {
					log.info("sendMessage{}", data);
					sink.success(true);
				}else {
					sink.success(false);
				}
			}, err->{
				log.info("sendMessage{}", err.getMessage().toString());
				sink.success(false);
			});
		});
	}
	
	public Mono<Boolean> sendMessage(List<String> numbers, String message){
		return Mono.create(sink ->{
			
		});
	}
}
