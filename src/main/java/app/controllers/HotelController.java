package app.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.dboperations.DBOperations;
import app.entities.Hotel;
import app.enums.HotelStatus;
import app.exceptions.ProcessTerminatedException;
import app.http.response.GenericResponse;
import app.models.CategoryMenuModel;
import app.models.HotelUserMapping;
import app.repositories.HotelRepository;
import app.repositories.UserRepository;
import app.services.HotelService;
import app.utilities.EmailService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController @RequestMapping("hotel")
public class HotelController {
	
	
	@Autowired private HotelService hotelService;
	@Autowired private HotelRepository hotelrepo;
	@Autowired private EmailService emailService;
	@Autowired private UserRepository userrepo;
    @Autowired private DBOperations dbops;
	

	@PostMapping("add")
	public Mono<GenericResponse<Object>> addHotel(@RequestBody HotelUserMapping mapping) {
		return hotelService.addHotel(mapping.getHotel(), mapping.getUser());
	}
	
	@GetMapping("approve/{id}")
	public Mono<GenericResponse<Object>> approveHotel(@PathVariable("id") String id, @RequestParam("url") String url){
		return Mono.create(sink ->{
			hotelrepo.findById(id).switchIfEmpty(Mono.fromRunnable( ()->{
				
			}))
			.subscribe(data ->{
				if(data.getUser().isVerified()) {
					data.setActive(false);
					data.setStatus(HotelStatus.APPROVED);
					userrepo.findById(data.getUser().getId()).subscribe(usr ->{
						usr.setActive(false);
						userrepo.save(usr).subscribe(u ->{
							data.setUser(u);
							hotelrepo.save(data).subscribe(hotel ->{
								emailService.sendApprovalMsg(data.getUser().getEmail(), "Activate your account", 
										"Please click below link/n/t "+url+"/verify/"+data.getUser().getEmail()).subscribe(mail ->{
											if(mail) {
												sink.success(GenericResponse.builder().body(data).code(ResponseCode.OK.name()).message("Hotel Approved Successfully").build());
											}else {
												sink.success(GenericResponse.builder().body(data).code(ResponseCode.OK.name()).message("Hotel Approved Successfully, But unable to send mail").build());
											}
										}, err->{
											sink. success(GenericResponse.builder().body(data).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
										});
							}, err ->{
								sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
							});
						}, err->{
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
						});
						
					}, err->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
					});
					
				}else {
					sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("Please Verify Hotel First").build());
				}
			}, err ->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("sendActivationEmail/{id}")
	public Mono<Boolean> sendActivationEmail(@PathVariable("id") String id, @RequestParam("url") String url){
		return Mono.create(sink->{
			userrepo.findById(id).subscribe(data ->{
				emailService.sendApprovalMsg(data.getEmail(), "Activate your account", 
						"Please click below link/n/t "+url+"/verify/"+data.getEmail()).subscribe(bool ->{
							if(bool) {
								sink.success(true);
							}else {
								sink.success(false);
							}
						}, err ->{
							
						});
			}, err ->{
				
			});
		});
		
	}
	
	@GetMapping("getRequestedHotelsByLimit/{limit}")
	public Flux<Hotel> getRequestedHotelsByLimit(@PathVariable("limit") int limit){
		return dbops.getLatestRequestedHotelsByLimit(limit);
		
	}
	
	@GetMapping("getApprovedHotelsByLimit/{limit}")
	public Flux<Hotel> getApprovedHotelsByLimit(@PathVariable("limit") int limit){
		return dbops.getLatestApprovedHotelsByLimit(limit);
		
	}
	
	@GetMapping("getVerifiedHotelsByLimit/{limit}")
	public Flux<Hotel> getVerifiedHotelsByLimit(@PathVariable("limit") int limit){
		return dbops.getLatestVerifiedHotelByLimit(limit);
		
	}
	
	@GetMapping("getPublishedHotelsByLimit/{limit}")
	public Flux<Hotel> getPublishedHotelsByLimit(@PathVariable("limit") int limit){
		return dbops.getLatestPublishedHotelByLimit(limit);
		
	}
	
	@GetMapping("getRejectedHotelsByLimit/{limit}")
	public Flux<Hotel> getRejectedHotelsByLimit(@PathVariable("limit") int limit){
		return dbops.getLatestRejectedHotelsByLimit(limit);
		
	}
	
	@GetMapping("/reject/{id}")
	public Mono<GenericResponse<Object>> rejectHotel(@PathVariable("id") String id){
		return Mono.create(sink ->{
			this.hotelrepo.findById(id)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message("Error in Finding Hotel with these details").build());
			}))
			.subscribe(data ->{
				userrepo.findById(data.getUser().getId()).subscribe(usr ->{
					usr.setActive(false);
					userrepo.save(usr).subscribe(fusr ->{
						data.setUser(fusr);
						data.setStatus(HotelStatus.REJECTED);
						data.setActive(false);
						hotelrepo.save(data).subscribe(fhot ->{
							sink.success(GenericResponse.builder().body(data).code(ResponseCode.OK.name()).message("Hotel Rejected Successfully").build());
						}, err->{
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
						});
					}, err ->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
					});
				}, err->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
				});
			}, err ->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("publish/{id}")
	public Mono<GenericResponse<Object>> publish(@PathVariable("id") String id){
		return Mono.create(sink ->{
			this.hotelrepo.findById(id)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message("Error in Finding Hotel with these details").build());
			}))
			.subscribe(data ->{
				userrepo.findById(data.getUser().getId()).subscribe(usr ->{
					usr.setActive(true);
					userrepo.save(usr).subscribe(fusr ->{
						data.setUser(fusr);
						data.setStatus(HotelStatus.PUBLISHED);
						data.setActive(true);
						hotelrepo.save(data).subscribe(fhot ->{
							sink.success(GenericResponse.builder().body(fhot).code(ResponseCode.OK.name()).message("Hotel Published Successfully").build());
						}, err->{
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
						});
					}, err ->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
					});
				}, err->{
					sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
				});
			}, err ->{
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("verifyhotel/{email}")
	public Mono<GenericResponse<Object>> verifyHotel(@PathVariable("email") String email){
		return Mono.create(sink -> {
			hotelrepo.findByUsername(email)
			.switchIfEmpty(Mono.fromRunnable(() -> {
				
			}))
			.subscribe(data -> {
				userrepo.findById(data.getUser().getId()).subscribe(usr -> {
					usr.setActive(true);	
					userrepo.save(usr).subscribe(fusr ->{
						data.setUser(fusr);
						data.setActive(false);
						data.setStatus(HotelStatus.VERIFIED);
						hotelrepo.save(data).subscribe(fhot ->{
							sink.success(GenericResponse.builder().body(fhot).code(ResponseCode.OK.name()).message("Hotel Verified Successfully").build());
						}, err ->{
							sink.success(GenericResponse.builder().body(null).code(ResponseCode.OK.name()).message(err.getMessage()).build());
						});
					}, err ->{
						sink.success(GenericResponse.builder().body(null).code(ResponseCode.OK.name()).message(err.getMessage()).build());
					});
				});
			},err -> {
				sink.success(GenericResponse.builder().body(null).code(ResponseCode.OK.name()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("getHotelByEmail/{email}")
	public Mono<GenericResponse<Object>> getHotel(@PathVariable("email") String email) {
		return Mono.create(sink -> {
			hotelrepo.findByUsername(email)
			.switchIfEmpty(Mono.fromRunnable(() -> {
				sink.success(GenericResponse.builder().code("NOT FOUND").body(null).message("Hotel  with this email not Found").build());
			}))
			.subscribe(hotel -> {
				
				sink.success(GenericResponse.builder().code("OK").body(hotel).message("HOtel Retrieved Successfully").build());
			}, err -> {
				sink.error(err);
			});
		});
	}
	
	@GetMapping("getCategoryMenus/{id}")
	public Flux<CategoryMenuModel> getCategoryMenus(@PathVariable("id") String id) {
		return Flux.create(sink -> {
			hotelrepo.findById(id)
			.switchIfEmpty(Mono.fromRunnable(() -> {
				sink.error(new ProcessTerminatedException("No hotel exist"));
			}))
			.subscribe(hotel -> {
				Set<String> categories = hotel.getMenus().stream().map(d -> d.getCategory()).collect(Collectors.toSet());
				Flux.fromIterable(categories).doOnComplete(() ->{
					sink.complete();
				}).subscribe(category ->{
					CategoryMenuModel m = CategoryMenuModel.builder()
							.menus(hotel.getMenus().stream().filter(menu -> menu.getCategory().equals(category)).collect(Collectors.toList()))
							.category(category)
							.build();
					sink.next(m);
				}, err->{
					sink.error(err);
				});
				
			}, err -> {
				sink.error(err);
			});
		});
	}
	
	@GetMapping("updateGSTAndPan/{gst}/{pan}/{id}")
	public Mono<GenericResponse<Object>> updateGSTandPANno(@PathVariable("gst") String gst, @PathVariable("pan") String pan, @PathVariable("id") String id) {
		return Mono.create(sink ->{
			hotelrepo.findById(id).switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("No Hotel available with given id").build());
			}))
			.subscribe(hotel ->{
				if(hotel.getStatus().equals(HotelStatus.VERIFIED) || hotel.getStatus().equals(HotelStatus.REQUESTEDFORPUBLISH)
						|| hotel.getStatus().equals(HotelStatus.PUBLISHED)) {
					hotel.setGstNo(gst);
					hotel.setPanNo(pan);
					hotelrepo.save(hotel).subscribe(data ->{
						sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).body(data).message("Hotel Updated Successfully").build());
					}, err->{
						sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
					});
				}
			},err->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
}
