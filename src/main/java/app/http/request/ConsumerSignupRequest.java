package app.http.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ConsumerSignupRequest {

	private String email;
	private String phoneNo;
	private String firstName;
	private String lastName;
	private String password;
}
