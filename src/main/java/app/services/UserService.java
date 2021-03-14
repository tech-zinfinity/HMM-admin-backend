package app.services;

import org.springframework.beans.factory.annotation.Autowired;

import app.entities.User;
import app.http.response.GenericResponse;
import app.repositories.UserRepository;
import app.utilities.OTPUtility;
import reactor.core.publisher.Mono;

public class UserService {

	@Autowired private UserRepository userrepo;
	@Autowired private OTPUtility otpUtility;
	
	public Mono<GenericResponse<Object>> registerUser(User user){
		return Mono.create(sink -> {
			
		});
	}
}
