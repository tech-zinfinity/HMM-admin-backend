package app.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Comment {

	private String userId;
	private String userName;
	private String phoneNo;
	
	@CreatedDate
	private LocalDateTime createdOn;
	
	private String comment;
}
