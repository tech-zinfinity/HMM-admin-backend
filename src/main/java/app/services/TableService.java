package app.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import app.entities.Hotel;
import app.entities.Table;
import app.http.response.GenericResponse;
import app.repositories.HotelRepository;
import app.utilities.FireStorageUtility;
import app.utilities.IDGeneratorWithTimeStamp;
import app.utilities.QRCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TableService {

	@Autowired private FireStorageUtility fireservice;
	@Autowired private QRCodeGenerator qrgenerator;
	@Autowired private HotelRepository hotelrepo;
	@Autowired private IDGeneratorWithTimeStamp idgen;
	
	
	public Mono<GenericResponse<Object>> registerTableForHotel(String hotelId, String tableNo, String tableId) {
		return Mono.create(sink ->{
			hotelrepo.findById(hotelId)
			.switchIfEmpty(Mono.fromRunnable( ()->{
				sink.success(GenericResponse.builder().body(null).code("NOT FOUND").message("No Hotel found").build());
			}))
			.subscribe(hotel ->{
				var list =  hotel.getTables();
				if(Objects.nonNull(list)) {
					list = new ArrayList<Table>();
					hotel.setTables(list);
				}
				JsonObject obj = new JsonObject();
				obj.addProperty("hotelId", hotelId);
				obj.addProperty("tableId", tableId);
				this.qrgenerator.generateQRCodeImage(obj.toString(), 200, 200)
				.switchIfEmpty(Mono.fromRunnable(() ->{
					sink.success(GenericResponse.builder().body(null).code(tableId).message("Failed in QR generation").build());
				}))
				.subscribe(file ->{
					sink.success(GenericResponse.builder().body(new InputStreamResource(file)).code(tableId).message("Completed").build());
				}, err->{
					err.printStackTrace();
					sink.error(err);
				});	
			}, err->{
				sink.error(err);
			});
		});
	}
}
