package app.services;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.constants.ResponseCode;
import app.entities.Hotel;
import app.entities.User;
import app.enums.HotelStatus;
import app.enums.Role;
import app.http.response.GenericResponse;
import app.repositories.HotelRepository;
import app.repositories.UserRepository;
import app.utilities.EmailService;
import reactor.core.publisher.Mono;

@Service
public class HotelService {

	@Autowired HotelRepository hotelrepo;
	@Autowired private UserRepository userrepo;
	@Autowired private EmailService emailService;
	
	//Add Hotel Service
	public Mono<GenericResponse<Object>> addHotel(Hotel hotel, User user){
		System.out.println(user.getEmail());
		return Mono.create(sink -> {
			this.userrepo.findByUsername(user.getEmail())
			.switchIfEmpty(Mono.fromRunnable( () ->{
				user.setUsername(user.getEmail());
				user.setActive(false);
				user.setVerified(true);
				user.setPassword("null");
				user.setRoles(Arrays.asList(Role.ROLE_HOTEL));
				if(user.getEmail() != null && !(user.getEmail().isBlank())) {
					userrepo.insert(user)
					.subscribe(usr ->{
						hotel.setStatus(HotelStatus.REQUESTED);
						hotel.setActive(false);
						hotel.setUser(usr);
						
						if(Objects.nonNull(hotel.getAddress())) {
							if(Objects.nonNull(hotel.getContactinfo())) {
								hotelrepo.insert(hotel).subscribe(hot ->{
									String[] r = {usr.getEmail()};
									emailService.sendSimpleMsg(r, "Confirmation Mail from HMM", "Your request is under consideration")
									.subscribe(sent ->{
										sink.success(GenericResponse.builder().body(hot).code("OK").message("Hotel Registered Successfully").build());
									}, e->{
										sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name())
												.message(e.getMessage()).build());
									});
								}, er ->{
									sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name())
											.message(er.getMessage()).build());

								});
							}else {
								sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name())
										.message("Please enter valid contact info").build());
							}
						}else {
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name())
									.message("Please enter valid address").build());

						}
					}, err->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name())
								.message(err.getMessage()).build());
					});
				}else {
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name())
							.message("Please enter valid email address").build());
				}

			}))
			.subscribe(data ->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.WARN.name())
						.message("This user is already registered, please use different email id").build());

			}, err ->{
				err.printStackTrace();
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name())
						.message("System is down, please try after some time").build());
			});
		});
	}
	
	//Delete Hotel Service
	public Mono<Void> deleteById(String id){
		return Mono.create(sink -> {
			hotelrepo.deleteById(id).subscribe(data -> {
				sink.success(data);
			});
		});
	}
	
	//Delete All Hotels Service
	public Mono<Boolean> deleteAllHotels(){
		return Mono.create(sink -> {
			hotelrepo.deleteAll().subscribe(data -> {
				if(data == null) {
					sink.success(true);
				}else {
					sink.success(false);
				}
			});
		});
	}
	
	//Update Hotel Service 
	public Mono<Hotel> updateHotel(Hotel hotel){
		return Mono.create(sink -> {
			hotelrepo.findById(hotel.getId()).subscribe(data -> {
				if(data != null){
					
				}else {
					sink.success(Hotel.builder().build());
				}
			}, err -> {
				sink.error(err);
			});
		});
	}
	
	
	//Activate/Publish Hotel Service 
	public Mono<Hotel> publishHotel(String id){
		return Mono.create(sink ->{
			hotelrepo.findById(id).subscribe(data-> {
				if(data.getName() != null ) {
					data.setActive(true);
					hotelrepo.save(data).subscribe(h -> {
						sink.success(h);
					});
				}else {
					sink.success(Hotel.builder().build());
				}	
			},err -> {
				sink.error(err);
			});
		});
	}
	
	
	//Deactivate/DE-Publish HOtel Service
	public Mono<Hotel> deactivateHotel(String id){
		return Mono.create(sink -> {
			hotelrepo.findById(id).subscribe(data ->{
				if(data.getName() != null) {
					data.setActive(false);
					hotelrepo.save(data).subscribe(h ->{
						sink.success(h);
					});
				}else {
					sink.success(Hotel.builder().build());
				}
			}, err -> {
				sink.error(err);
			});
		});
	}
	
	
}
