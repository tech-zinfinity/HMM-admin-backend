package app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
				Mono.just(notification)
				.switchIfEmpty(Mono.fromRunnable( ()->{
					sink.error(new ProcessTerminatedException("Database issue"));
				})).subscribe(no ->{
					List<String> emails = new ArrayList<String>();
					List<String> numbers = new ArrayList<String>();
					userrepo.findAllById(notification.getUserIds())
					.switchIfEmpty(Mono.fromRunnable( ()->{
						sink.error(new ProcessTerminatedException("No users selected"));
					}))
					.doOnComplete(() ->{
						boolean message  = false;
						boolean email = false;
						if(notification.getModes().contains(NotificationModeEnum.MESSAGE)) {
							message = this.sendOnMessage(numbers, no.getMessage()).block();
						}
						if(notification.getModes().contains(NotificationModeEnum.EMAIL)) {
							email = this.sendOnEmail(emails, notification.getMessage(), "").block();
						}
						
						Map<NotificationModeEnum, Boolean> m = new HashMap<NotificationModeEnum, Boolean>();
						m.put(NotificationModeEnum.MESSAGE, message);
						m.put(NotificationModeEnum.EMAIL, email);
						no.setStatusMap(m);
						if(notification.getModes().contains(NotificationModeEnum.NATIVE)) {
							no.setCreatedOn(LocalDateTime.now());
							notificationrepo.save(no).subscribe(da ->{
								sink.success(da);
							}, er->{
								sink.error(er);
							});
						}else {
							sink.success();
						}

					})
					.subscribe(user ->{
						if(Objects.nonNull(user.getPhoneNo())) {
							System.out.println(user.getPhoneNo());
							numbers.add(user.getPhoneNo());
						}if(Objects.nonNull(user.getEmail())) {
							emails.add(user.getEmail());
						}
						
					}, err->{
						sink.error(err);
					});
				}, err->{
					sink.error(err);
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
		return notificationrepo.findByUserIds(userId).sort((o1,o2)->o2.getCreatedOn().compareTo(o1.getCreatedOn()));
	}
	
	public Mono<Object> removeNotificationForUser(String id, String userId) {
		return Mono.create(sink ->{
			notificationrepo.findById(id)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.error(new ProcessTerminatedException("No Notification Available"));
			})).subscribe(data ->{
				System.out.println(data);
				var users  = data.getUserIds();
				if(users.size() == 1) {
					System.out.println(users);
					notificationrepo.delete(data)
					.doOnSuccess(onSuccess->{
						System.out.println("on success");
						sink.success(data);
					}).doOnError(onError->{
						sink.error(onError);
					}).subscribe();
				}else {
					users.remove(userId);
					data.setUserIds(users);
					notificationrepo.save(data).subscribe(d ->{
						sink.success(data);
					}, err->{
						sink.error(err);
					});
				}
			}, err->{
				
			});
		});
	}
}
