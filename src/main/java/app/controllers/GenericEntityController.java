package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import app.dboperations.GenericEntityService;
import reactor.core.publisher.Flux;

@CrossOrigin(allowedHeaders="*")
@RestController @RequestMapping("gen")
public class GenericEntityController {
		
	@Autowired private GenericEntityService service;
	
	@GetMapping("findAll/{table}")
	public Flux<?> findAll(@PathVariable("table") String table) throws ClassNotFoundException {
		return service.findAll(table);
	}
}
