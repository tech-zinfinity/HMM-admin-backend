package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.entities.Hotel;
import app.enums.HotelStatus;
import app.http.response.GenericResponse;
import app.models.HotelUserMapping;
import app.repositories.HotelRepository;
import app.services.HotelService;
import app.utilities.EmailService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Mono;

@RestController @RequestMapping("hotel")
public class HotelController {
	
	@Autowired private HotelService hotelService;
	@Autowired private HotelRepository hotelrepo;
	@Autowired private EmailService emailService;

	@PostMapping("add")
	public Mono<GenericResponse<Object>> addHotel(@RequestBody HotelUserMapping mapping) {
		return hotelService.addHotel(mapping.getHotel(), mapping.getUser());
	}
	
	@GetMapping("approve/{id}/")
	public Mono<GenericResponse<Object>> approveHotel(@PathVariable("id") String id, @RequestParam("url") String url){
		return Mono.create(sink ->{
			hotelrepo.findById(id).switchIfEmpty(Mono.fromRunnable( ()->{
				
			}))
			.subscribe(data ->{
				if(data.getUser().isVerified()) {
					data.setActive(false);
					data.setStatus(HotelStatus.APPROVED);
					var u = data.getUser();
					u.setActive(true);
					data.setUser(u);
					
					hotelrepo.save(data).subscribe(hotel ->{
						
					}, err ->{
						
					});
				}else {
					//please verify hotel
				}
			}, err ->{
				
			});
		});
	}
	
}
