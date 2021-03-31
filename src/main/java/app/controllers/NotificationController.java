package app.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.entities.Notification;
import app.repositories.NotificationRepository;
import app.repositories.UserRepository;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notify")
public class NotificationController {

	private NotificationRepository notificationrepo;
	private UserRepository userrepo;
	
	
//	public Mono<Object> addNotification(@RequestBody Notification notification) {
//		
//	}
//	
//	public Mono<Object> getNotificationsForUser(@PathVariable("id") String id){
//		
//	}
}
