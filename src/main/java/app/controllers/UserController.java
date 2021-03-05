package app.controllers;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.entities.User;
import app.enums.HotelStatus;
import app.http.response.GenericResponse;
import app.repositories.HotelRepository;
import app.repositories.UserRepository;
import app.utilities.EmailService;
import app.utilities.OTPUtility;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
@CrossOrigin(allowedHeaders="*")
public class UserController {

	@Autowired private OTPUtility otpUtility;
	@Autowired private UserRepository userrepo;
	@Autowired private EmailService emailService;
	@Autowired private HotelRepository hotelrepo;
	
	@GetMapping("sendotp/{emailid}")
	public Mono<GenericResponse<Object>> sendOtp(@PathVariable("emailid") String emailid){
		System.out.println(emailid);
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
				emailService.sendOTP(emailid, "Please verify OTP", String.valueOf(otpUtility.generateOTP(emailid))).subscribe(ot->{
					if(ot) {
						sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).message("Sent Successfully").body(null).build());
					}else {
						sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("Otp sending failed").body(null).build());
					}
				}, er->{
					sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(er.getMessage()).body(null).build());
				});
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
	
	@PostMapping("signup")
	public Mono<GenericResponse<Object>> registerUser(@RequestBody User user){
		return Mono.create(sink->{
			if(!user.getEmail().isBlank() && !user.getPassword().isBlank()) {
				userrepo.findByEmail(user.getEmail())
				.switchIfEmpty(Mono.fromRunnable( ()->{
					String[] roles = {"ADMIN"};
					user.setRoles(roles);
					user.setActive(true);
					userrepo.insert(user).subscribe(data ->{
						sink.success(GenericResponse.builder().body(data).code(ResponseCode.OK.name()).message("User added successfully").build());
					}, er ->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(er.getMessage()).build());

					});
				}))
				.subscribe(data ->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name()).message("User email is alreday registered").build());

				}, err ->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());

				});
			}else {
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name()).message("Email id or Password field is null or not enetered").build());

			}
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
						if(data.getPassword().equals(password)) {
							sink.success(GenericResponse.builder().body(data).code(ResponseCode.OK.name()).message("Login successfull").build());
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
	
	@GetMapping("activate/{password}/{email}")
	public Mono<GenericResponse<Object>> activateHotelAccount(@PathVariable("password") String password, @PathVariable("email") String email) {
		return Mono.create(sink ->{
			hotelrepo.findByUsername(email)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name()).message("No user found").build());
			}))
			.subscribe(hot ->{
				userrepo.findByEmail(email).subscribe(usr ->{
					usr.setActive(true);
					usr.setPassword(password);
					userrepo.save(usr).subscribe(fusr->{
						hot.setUser(fusr);
						hot.setStatus(HotelStatus.VERIFIED);
						hot.setActive(false);
						hotelrepo.save(hot).subscribe(fhot->{
							sink.success(GenericResponse.builder().body(fhot).code(ResponseCode.OK.name()).message("Activated Successfully").build());
						}, e->{
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(e.getMessage()).build());
						});
					}, er->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(er.getMessage()).build());

					});
				}, er->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(er.getMessage()).build());

				});
			}, err->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());

			});
		});
	}
}
