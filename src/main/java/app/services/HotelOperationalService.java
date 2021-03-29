package app.services;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.entities.Constants;
import app.entities.Menu;
import app.http.response.GenericResponse;
import app.repositories.ConstantsRepository;
import app.repositories.HotelRepository;
import app.utilities.IDGeneratorWithTimeStamp;
import io.swagger.v3.oas.models.media.ArraySchema;
import reactor.core.publisher.Mono;

@Service
public class HotelOperationalService {
	
	@Autowired private HotelRepository hotelrepo;
	@Autowired private IDGeneratorWithTimeStamp idgen;
	@Autowired private ConstantsRepository consrepo;
	
	public Mono<GenericResponse<Object>> addMenubyCategory(String id,Menu menu){
		return Mono.create(sink -> {
			hotelrepo.findById(id)
			.switchIfEmpty(Mono.fromRunnable(() -> {
				sink.success(GenericResponse.builder().code("WARN").message("Unable to Find Hotel").body(null).build());
			}))
			.subscribe(hotel -> {
				String menuid = idgen.generateID();
				menu.setId(menuid);
				if(!Objects.nonNull(hotel.getMenus())) {
					var li = Arrays.asList(menu);
					hotel.setMenus(li);
				}else {
					var menulist = hotel.getMenus();
					menulist.add(menu);
					hotel.setMenus(menulist); 
				}
				hotelrepo.save(hotel).subscribe(savedmenu -> {
					  sink.success(GenericResponse.builder().code("OK").message("Menu Added Successfully").body(hotel).build());
					} , err-> {
					  sink.error(err);
					});
			},err -> {
				sink.error(err);
			});
		});
	}
	
	
	public Mono<GenericResponse<Object>> addCategory(String key,String value) {
		return Mono.create(sink -> {
			consrepo.findByKey(key)
			.switchIfEmpty(Mono.fromRunnable(() -> {
				Constants obj = Constants.builder().key(key).value(Arrays.asList(value)).build();
				consrepo.insert(obj).subscribe(data -> {
					sink.success(GenericResponse.builder().code("OK").message("Category Available").body(data).build());
				},err -> {
					sink.error(err);
				});
			}))
			.subscribe(constant -> {
				if(!Objects.nonNull(constant.getValue())) {
					var vallist =  Arrays.asList(value);
					constant.setValue(vallist);
				}else {
					if(constant.getValue().stream().filter(c -> c.equalsIgnoreCase(value)).count() >0) {
						sink.success(GenericResponse.builder().code("Warn").message("Already Available").body(null).build());
					}else {
						var val = constant.getValue();
						val.add(value);
						constant.setValue(val);
					}
					
				}
				consrepo.save(constant).subscribe(savedconstant -> {
					sink.success(GenericResponse.builder().code("OK").message("Added Successfully").body(savedconstant).build());
				},err-> {
					sink.error(err);
				});
			},err -> {
				sink.error(err);
			});
		});
	}
	
	
}
