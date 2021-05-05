package app.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.constants.ResponseCode;
import app.entities.SupportTicket;
import app.http.response.GenericResponse;
import app.models.Comment;
import app.repositories.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class SupportService {

	@Autowired private UserRepository userrepo;
	
	
	public Mono<GenericResponse<Object>> verifySupportTicket(SupportTicket ticket) {
		return Mono.create(sink ->{
			userrepo.findById(ticket.getRequester().getId()).subscribe(user ->{
				if(!user.isVerified()) {
					sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("User is not verified").build());
				}else if(!user.isActive()) {
					sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("User is not active").build());
				}else {
					if(!Objects.nonNull(ticket.getTitle())) {
						sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Title is not available").build());
					}else if(!Objects.nonNull(ticket.getDescription())) {
						sink.success(GenericResponse.builder().code(ResponseCode.WARN.toString()).message("Deacription is not available").build());
					}else {
						sink.success(GenericResponse.builder().code(ResponseCode.OK.toString()).message("OK").body(ticket).build());
					}
				}
			}, err ->{
				sink.success(GenericResponse.builder().code(ResponseCode.ERR.toString()).message("User is not vailable").build());
			});
		});
	}
	
	public boolean verifyComment(Comment comment) {
		var bools =true;
		if(!Objects.nonNull(comment.getComment())) {
			bools=false;
		}
		if(!Objects.nonNull(comment.getUserId())) {
			bools= false;
		}
		return bools;
	}
}
