package app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entities.User;
import app.repositories.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class UserService {
	
	@Autowired UserRepository userrepo;
	
	public Mono<User> findByUsername(String username) {
		return userrepo.findByUsername(username).flatMap(data ->{
			if(data instanceof User) {
				return Mono.just(data);
			}
			return Mono.empty();
		});
	}
	
}
