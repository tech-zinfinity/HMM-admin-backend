package app.models;

import app.entities.Hotel;
import app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class HotelUserMapping {

	private Hotel hotel;
	private User user;
}
