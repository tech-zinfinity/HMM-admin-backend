package app.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import app.enums.NotificationModeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Document(collection = "Notification")
public class Notification {

	@Id
	private String id;
	private String message;
	
	@LastModifiedDate
	private LocalDateTime createdOn;
	private String redirectionURL;
	private List<NotificationModeEnum> modes;
	private List<String> userIds;
	private Map<NotificationModeEnum, Boolean> statusMap;
	
}
