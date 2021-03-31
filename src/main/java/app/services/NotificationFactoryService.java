package app.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entities.Notification;
import app.enums.NotificationModeEnum;
import app.exceptions.ProcessTerminatedException;
import app.repositories.NotificationRepository;
import app.repositories.UserRepository;
import app.utilities.EmailService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NotificationFactoryService {

	@Autowired private NotificationRepository notificationrepo;
	@Autowired private MessagingService messagingservice;
	@Autowired private EmailService emailservice;
	@Autowired private UserRepository userrepo;
	
	
	public Mono<Notification> sendNotification(Notification notification){
		return Mono.create(sink ->{
			this.verifyNotification(notification).subscribe(not ->{
				notificationrepo.save(not)
				.switchIfEmpty(Mono.fromRunnable( ()->{
					sink.error(new ProcessTerminatedException("Database issue"));
				})).subscribe(no ->{
					List<String> emails = new ArrayList<String>();
					List<String> numbers = new ArrayList<String>();
					userrepo.findAllById(notification.getUserIds())
					.switchIfEmpty(Mono.fromRunnable( ()->{
						notificationrepo.delete(no).subscribe(r ->{
							sink.error(new ProcessTerminatedException("No users selected"));
						}, err->{
							sink.error(err);
						});
					}))
					.doOnComplete(() ->{
						Mono<Boolean> message  = Mono.empty();
						Mono<Boolean> email = Mono.empty();
						if(notification.getModes().contains(NotificationModeEnum.MESSAGE)) {
							message = this.sendOnMessage(numbers, notification.getMessage());
						}
						if(notification.getModes().contains(NotificationModeEnum.EMAIL)) {
							email = this.sendOnEmail(emails, notification.getMessage(), "");
						}
						message.zipWith(email).subscribe(s ->{
							Map<NotificationModeEnum, Boolean> m = new HashMap<NotificationModeEnum, Boolean>();
							m.put(NotificationModeEnum.MESSAGE, s.getT1());
							m.put(NotificationModeEnum.EMAIL, s.getT2());
							no.setStatusMap(m);
							notificationrepo.save(no).subscribe(da ->{
								sink.success(da);
							}, er->{
								sink.error(er);
							});
						}, err->{
							
						});
					})
					.subscribe(user ->{
						numbers.add(user.getPhoneNo());
						emails.add(user.getEmail());
					}, err->{
						sink.error(err);
					});
				}, err->{
					
				});
			}, err->{
				sink.error(err);
			});
		});
	}
	
	private Mono<Boolean> sendOnMessage(List<String> numbers, String message){
		return Mono.create(sink ->{
			messagingservice.sendMessage(numbers, message)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(false);
			}))
			.subscribe(data ->{
				if(data) {
					sink.success(true);
				}else {
					sink.success(false);
				}
			}, err->{
				log.error(err.getMessage());
				sink.success(false);
			});
		});
	}
	
	private Mono<Boolean> sendOnEmail(List<String> emails, String message, String subject){
		return Mono.create(sink ->{
			emailservice.sendSimpleMsg(emails, subject, message)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(false);
			}))
			.subscribe(data ->{
				if(data) {
					sink.success(true);
				}else {
					sink.success(false);
				}
			}, err->{
				log.error(err.getMessage());
				sink.success(false);
			});
		});
	}
	
	private Mono<Notification> verifyNotification(Notification notification) {
		return Mono.create(sink ->{
			if(!Objects.nonNull(notification.getMessage())) {
				sink.error(new ProcessTerminatedException("Message is null"));
			}
			if(Objects.nonNull(notification.getModes())) {
				if(notification.getModes().size()<=0) {
					sink.error(new ProcessTerminatedException("Modes are empty"));
				}
			}else {
				sink.error(new ProcessTerminatedException("Modes are empty"));
			}
			if(Objects.nonNull(notification.getUserIds())) {
				if(notification.getUserIds().size()<=0) {
					sink.error(new ProcessTerminatedException("No user selected"));
				}
			}else {
				sink.error(new ProcessTerminatedException("No user selected"));
			}
			sink.success(notification);
		});
		
	}
	
	public Flux<Notification> getNotificationByUserId(String userId){
		return notificationrepo.findByUserIds(userId);
	}
	
	public Mono<Object> deleteNotification(String id, String userId) {
		return Mono.create(sink ->{
			notificationrepo.findById(id).switchIfEmpty(Mono.fromRunnable( ()->{
				sink.error(new ProcessTerminatedException(""));
			})).subscribe(data ->{
				
			}, err->{
				
			});
		});
	}
}
