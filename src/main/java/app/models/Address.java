package app.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Address {

	private String line1;
	private String line2;
	private String landmark;
	private int pincode;
	private String city;
	private String state;
	private String country;

}
