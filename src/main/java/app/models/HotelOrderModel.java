package app.models;

import app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelOrderModel {

	private String id;
	private String name;
	private Address address;
	private ContactInfo contactinfo;
	private String photo;
	private String userId;
	private Double[] location;
	private String gstNo;
	private String panNo;

}
