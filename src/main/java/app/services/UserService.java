package app.services;

import org.springframework.beans.factory.annotation.Autowired;

import app.repositories.UserRepository;
import app.utilities.OTPUtility;
import reactor.core.publisher.Mono;

public class UserService {

	@Autowired private UserRepository userrepo;
	@Autowired private OTPUtility otpUtility;
	
}
