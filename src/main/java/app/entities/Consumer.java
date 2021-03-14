package app.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "Consumer")
public class Consumer {

	@Id
	private String id;
	private String userId;
	private List<Order> orders;
	private String firstName;
	private String lastName;
}
