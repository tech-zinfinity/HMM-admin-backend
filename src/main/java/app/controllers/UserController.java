package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.http.response.GenericResponse;
import app.repositories.UserRepository;
import app.utilities.EmailService;
import app.utilities.OTPUtility;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired private OTPUtility otpUtility;
	@Autowired private UserRepository userrepo;
	@Autowired private EmailService emailService;
	
	@GetMapping("sendotp/{emailid}")
	public Mono<GenericResponse<Object>> sendOtp(@PathVariable("emailid") String emailid){
		return Mono.create(sink ->{
			userrepo.findByEmail(emailid)
			.switchIfEmpty(Mono.fromRunnable(()->{
				emailService.sendOTP(emailid, "Please verify OTP", String.valueOf(otpUtility.generateOTP(emailid))).subscribe(ot->{
					if(ot) {
						sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).message("Sent Successfully").body(null).build());
					}else {
						sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("Otp sending failed").body(null).build());
					}
				}, er->{
					sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(er.getMessage()).body(null).build());
				});
			}))
			.subscribe(data -> {
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("Email is already registered").body(null).build());
			} ,err -> {
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).body(null).build());
			});
		});
	}
	
	@GetMapping("verifyotp/{emailid}/{otp}")
	public Mono<Boolean> verifyOtp(@PathVariable("emailid")String emailid, @PathVariable("otp")String otp){
		return Mono.create(sink ->{
			sink.success(otpUtility.verifyOTP(emailid, Integer.valueOf(otp)));
		});
	}
}
