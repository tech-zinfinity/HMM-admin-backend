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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.dboperations.DBOperations;
import app.entities.Table;
import app.http.response.GenericResponse;
import app.repositories.HotelRepository;
import app.repositories.UserRepository;
import app.services.HotelService;
import app.services.TableService;
import app.utilities.EmailService;
import app.utilities.IDGeneratorWithTimeStamp;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController()
@RequestMapping("ops")
public class HotelOperationalController {

	
	@Autowired private HotelRepository hotelrepo;
	@Autowired private TableService tableserice;
	
	
	@GetMapping("registerTable/{hotelId}/{tableNo}/{tableId}")
	public Mono<ResponseEntity<Object>> registerTable(@PathVariable("hotelId") String hotelId, @PathVariable("tableNo") String tableNo
			, @PathVariable("tableId") String tableId) {
		System.out.println("coming inside");
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
}
