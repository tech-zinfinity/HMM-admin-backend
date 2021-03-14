package app.http.response;

import java.util.List;

import app.entities.Order;
import app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ConsumerResponse {

	private String id;
	private List<Order> orders;
	private String firstName;
	private String lastName;
	private User user;
}
