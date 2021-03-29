package app.controllers;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.dboperations.DBOperations;
import app.entities.Constants;
import app.entities.Hotel;
import app.entities.Menu;
import app.entities.Table;
import app.http.response.GenericResponse;
import app.repositories.ConstantsRepository;
import app.repositories.HotelRepository;
import app.repositories.UserRepository;
import app.services.HotelOperationalService;
import app.services.HotelService;
import app.services.TableService;
import app.utilities.EmailService;
import app.utilities.IDGeneratorWithTimeStamp;
import ch.qos.logback.core.db.dialect.SybaseSqlAnywhereDialect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController()
@RequestMapping("ops")
public class HotelOperationalController {

	
	@Autowired private HotelService hotelService;
	@Autowired private HotelOperationalService hotelopservice;
	@Autowired private HotelRepository hotelrepo;
	@Autowired private TableService tableserice;
	
	@Autowired private IDGeneratorWithTimeStamp idgen;
	@Autowired private ConstantsRepository consrepo;
	
	@GetMapping("registerTable/{hotelId}/{tableNo}/{tableId}")
	public Mono<ResponseEntity<Object>> registerTable(@PathVariable("hotelId") String hotelId, @PathVariable("tableNo") String tableNo
			, @PathVariable("tableId") String tableId) {
		return Mono.create(sink ->{
			tableserice.registerTableForHotel(hotelId, tableNo, tableId).subscribe(data ->{
				sink.success(
						ResponseEntity.ok()
		                .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=" + data.getCode())
		                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
		                .body(data.getBody()));
			}, err->{
				sink.error(err);
			});
		});
	}
	
	@GetMapping("registerTableQR/{hotelId}/{tableNo}/{URL}/{id}")
	public Mono<GenericResponse<Object>> registerTableQR(@PathVariable("hotelId") String hotelId, @PathVariable("tableNo") String tableNo
			, @PathVariable("URL") String URL, @PathVariable("id") String id) {
		return Mono.create(sink ->{
			var table = Table.builder()
					.hotelId(hotelId)
					.tableNo(tableNo)
					.tableId(id)
					.qrLink(URL)
					.build();
			this.hotelrepo.findById(hotelId)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				
			}))
			.subscribe(d ->{
				var list = d.getTables();
				if(!Objects.nonNull(list)) {
					list = new ArrayList<Table>();
				}
				list.add(table);
				d.setTables(list);
				hotelrepo.save(d).subscribe(hotel ->{
					sink.success(GenericResponse.builder().code("Success").message("Success").body(hotel).build());
				}, err->{
					sink.error(err);
				});
			}, err->{
				sink.error(err);
			});
			
		});
	}
	
	@PostMapping("addMenubyCategory/{id}")
	public Mono<GenericResponse<Object>> addMenubyCategory(@PathVariable String id,@RequestBody Menu menu) {
		return hotelopservice.addMenubyCategory(id, menu);
	}
	
	@GetMapping("getMenusByHotelId/{id}")
	public Mono<Object> getMenuByHotelId(@PathVariable("id") String id) {
			return hotelrepo.findById(id)
					.switchIfEmpty(Mono.just(new Hotel()))
					.map(hotel -> hotel.getMenus());
	}
	
	@GetMapping("addKeyandValue/{key}/{value}")
	public Mono<GenericResponse<Object>> addCategory(@PathVariable("key") String key,@PathVariable("value") String value){
		return hotelopservice.addCategory(key, value);
	}
	
	@GetMapping("getAllCategories/{key}")
	public Mono<Object> getAllCategories(@PathVariable("key") String key){
		return consrepo.findByKey(key)
				.switchIfEmpty(Mono.just(new Constants()))
				.map(data -> data.getValue());
	}
	
	@GetMapping("/deleteTable/{tableId}/{hotelId}")
	public Mono<Object> deleteTable(@PathVariable("tableId") String tableId, @PathVariable("hotelId") String hotelId){
		return Mono.create(sink ->{
			hotelrepo.findById(hotelId).switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("No Hotel available").build());
			})).subscribe(d->{
				var list = d.getTables();
				list.removeIf(e -> e.getTableId().equals(tableId));
				d.setTables(list);
				hotelrepo.save(d).subscribe(s ->{
					sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).message("Table deleted successfully").body(s).build());
				}, err->{
					sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
				});
			}, err->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
	
	@GetMapping("/deleteMenu/{menuId}/{hotelId}")
	public Mono<Object> deleteMenu(@PathVariable("menuId") String menuId, @PathVariable("hotelId") String hotelId){
		return Mono.create(sink ->{
			hotelrepo.findById(hotelId).switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().code(ResponseCode.WARN.name()).message("No Hotel available").build());
			})).subscribe(d->{
				var list = d.getMenus();
				list.removeIf(e -> e.getId().equals(menuId));
				d.setMenus(list);
				hotelrepo.save(d).subscribe(s ->{
					sink.success(GenericResponse.builder().code(ResponseCode.OK.name()).message("Menu deleted successfully").body(s).build());
				}, err->{
					sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
				});
			}, err->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.name()).message(err.getMessage()).build());
			});
		});
	}
}
