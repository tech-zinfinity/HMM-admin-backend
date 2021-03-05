package app.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import app.enums.HotelStatus;
import app.models.Address;
import app.models.ContactInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection="HOTEL")
public class Hotel {
	
	@Id
	private String id;
	private String name;
	private Address address;
	private ContactInfo contactinfo;
	@Builder.Default
	private boolean active = false;
	private Double[] location;
	private HotelStatus status;
	private boolean deleted;
	private String photo;
	private User user;
	
	private List<Menu> menus;
	private List<Table> tables;
}
