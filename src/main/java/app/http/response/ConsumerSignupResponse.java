package app.http.response;

import java.util.List;

import app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class ConsumerSignupResponse {

	private ConsumerResponse consumer;
	private List<Role> roles;
	private String token;
}
