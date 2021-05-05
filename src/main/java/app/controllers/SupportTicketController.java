package app.controllers;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.constants.ResponseCode;
import app.entities.SupportTicket;
import app.enums.SupportTicketStatus;
import app.exceptions.ProcessTerminatedException;
import app.http.response.GenericResponse;
import app.models.Comment;
import app.repositories.SupportTicketRepository;
import app.repositories.UserRepository;
import app.services.SupportService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(allowedHeaders="*")
@RestController
@RequestMapping("support")
public class SupportTicketController {

	/*
	 * addSupport
	 * updateSupport
	 * addComment
	 * getTicketById,
	 * getTicketsByRequesterId,
	 * getOpenSupporttickets
	 * */
	
	
	@Autowired UserRepository userrepo;
	@Autowired SupportTicketRepository supportrepo;
	@Autowired SupportService supportservice;
	
	
	@PostMapping("add")
	public Mono<GenericResponse<Object>> addTicket(@RequestBody SupportTicket ticket) {
		return Mono.create(sink ->{
			supportservice.verifySupportTicket(ticket).subscribe(d ->{
				if(d.getCode().equals(ResponseCode.OK.toString())) {
					var t = (SupportTicket) d.getBody();
					t.setStatus(SupportTicketStatus.OPEN);
					t.setActive(true);
					supportrepo.insert(t).subscribe(e ->{
						sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("OK").body(ticket).build());
					}, err->{
						sink.success(GenericResponse.builder().code(ResponseCode.ERR.toString()).message(err.getMessage()).build());
					});
				}else {
					sink.success(d);
				}
			});
		});
	}
	
	@PostMapping("update")
	public Mono<GenericResponse<Object>> updateTicket(@RequestBody SupportTicket ticket) {
		return Mono.create(sink ->{
			
		});
	}
	
	@PostMapping("addComment/{id}")
	public Mono<SupportTicket> addComment(@RequestBody Comment comment, @PathVariable("id") String id) {
		return Mono.create(sink ->{
			if(supportservice.verifyComment(comment)) {
				supportrepo.findById(id).subscribe(d ->{
					if(!Objects.nonNull(d.getComment())) {
						d.setComment(new ArrayList<Comment>());
					}
					var l = d.getComment();
					l.add(comment);
					d.setComment(l);
					supportrepo.save(d).subscribe(e->{
						sink.success(e);
					}, err->{
						sink.error(new ProcessTerminatedException("Problem while saving ticket"));
					});
				}, err->{
					sink.error(new ProcessTerminatedException("No ticket available"));
				});
			}else {
				sink.error(new ProcessTerminatedException("Comment or user is null"));
			}
		});
	}
	
	@GetMapping("getTicketById/{id}")
	public Mono<SupportTicket> getTicketById(@PathVariable("id") String id) {
		return supportrepo.findById(id);
	}
	
	@GetMapping("getTicketsByRequesterId/{id}")
	public Flux<SupportTicket> getTicketsByRequesterId(@PathVariable("id") String id) {
		return supportrepo.findByRequestedUserId(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("getTicketsByStatus/{status}")
	public Flux<SupportTicket> getOpenSupporttickets(@PathVariable("status") String status) {
		return supportrepo.findByStatus(status);
	}
}
