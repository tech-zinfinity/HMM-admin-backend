package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.entities.Notification;
import app.repositories.NotificationRepository;
import app.repositories.UserRepository;
import app.services.NotificationFactoryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("notify")
@CrossOrigin(allowedHeaders="*")
public class NotificationController {

	private NotificationRepository notificationrepo;
	private UserRepository userrepo;
	@Autowired private NotificationFactoryService notificationservice;
	
	@PostMapping("publish")
	public Mono<Notification> addNotification(@RequestBody Notification notification) {
		return notificationservice.sendNotification(notification);
	}
	
	@GetMapping("getForUser/{id}")
	public Flux<Notification> getNotificationsForUser(@PathVariable("id") String id){
		return notificationservice.getNotificationByUserId(id);
	}
	
	@GetMapping("removeForUser/{id}/{userId}")
	public Mono<Object> removeNotification(@PathVariable("id") String id, @PathVariable("userId") String userId){
		return notificationservice.removeNotificationForUser(id, userId);
	}
}
