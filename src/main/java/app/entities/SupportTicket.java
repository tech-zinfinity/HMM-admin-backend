package app.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import app.enums.SupportTicketStatus;
import app.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "SupportTicket")
public class SupportTicket {

	@Id
	private String id;
	private String title;
	private String description;
	private String priority;
	
	private List<Comment> comment;
	
	private boolean active;
	private SupportTicketStatus status;
	private User requester;
	private User reviewver;
	
	@CreatedDate
	private LocalDateTime createdOn;
	@LastModifiedDate
	private LocalDateTime updatedOn;
	
	@CreatedBy
	private Object createdBy;
}
